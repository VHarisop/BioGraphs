#!/usr/bin/env bash

num_kgraphs=${1:-20}

BASE_DIR="${HOME}/demokritos/BioGraphs"
EXPERIMENT="gr.demokritos.biographs.experiments.TestTrie"
DATA_DIR="${BASE_DIR}/scripts/datasets/synthetic_${num_kgraphs}K"

# result directory - if it doesn't exist, create it!
RESULT_DIR="${BASE_DIR}/scripts/results/synthetic_${num_kgraphs}K"
if [ ! -d "$RESULT_DIR" ]; then
	mkdir -p "$RESULT_DIR"
fi

NCL_FILE="${DATA_DIR}/ncl.fa"
SCRIPT="${BASE_DIR}/scripts/tools/parse_dna_results.py"

# set max value for JVM heap
export MAVEN_OPTS=-Xmx4096m

# run a clean compilation in case any updates have been made to the codebase
mvn clean compile
[[ $? -ne  0 ]] && exit


for mutations in 1 2 3 4; do
	# Run the experiment for all combos, parse results and remove temp files
	mut_file="${DATA_DIR}/mut_${mutations}.fa"
	mvn exec:java \
		-Dexec.mainClass=${EXPERIMENT} \
		-Dexec.args="${NCL_FILE} ${mut_file} ${mutations}" \
		| grep -v -e "INFO" -e "WARNING" \
		> "${RESULT_DIR}/correct_trie_${mutations}.json"
	python3 ${SCRIPT} \
		-r ${RESULT_DIR}/correct_trie_${mutations}.json \
		| awk -F ' ' '{print $2}' \
		| xargs printf "%s\n"
done


# reset maven opts to use normal max heap size
unset MAVEN_OPTS
