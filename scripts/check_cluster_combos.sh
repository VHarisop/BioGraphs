#!/usr/bin/env bash

BASE_DIR="${HOME}/demokritos/BioGraphs"
MAIN="gr.demokritos.biographs.experiments.TestClusterHybrid"
WORDS="${BASE_DIR}/scripts/datasets/words.txt"
MUTATIONS="${BASE_DIR}/scripts/datasets/word_mutations.txt"
GROUND_FILE="${BASE_DIR}/scripts/datasets/words.txt_c_1_mutated.txt"
SCRIPT="${BASE_DIR}/scripts/tools/parse_results.py"

# run a clean compilation
mvn clean compile
[[ $? -ne  0 ]] && exit

# Run the experiment for all combos, parse results and remove temp files
for clusters in 35 30 25 20 10; do
	for iters in 80 100 150 200 500; do
		mvn exec:java \
			-Dexec.mainClass=${MAIN} \
			-Dexec.args="${WORDS} ${MUTATIONS} ${clusters} ${iters}" \
			| grep -v -e "INFO" -e "WARNING" > test_${clusters}_${iters}.out

		# run python script to parse results
		python3 ${SCRIPT} -g ${GROUND_FILE} -r test_${clusters}_${iters}.out \
			| awk -F ' ' '{print $2}' \
			| xargs printf "Cluster (%d, %d): %s - " ${clusters} ${iters}

		# print bin sizes as well
		python3 ${SCRIPT} -g ${GROUND_FILE} -r test_${clusters}_${iters}.out \
			| awk -F ' ' '{$1=$2=""; print $0}'

		# print bin size and remove temp file
		rm test_${clusters}_${iters}.out
	done
done
