#!/usr/bin/env bash

BASE_DIR="${HOME}/demokritos/BioGraphs"
MAIN="gr.demokritos.biographs.experiments.TestCluster"
WORDS="${BASE_DIR}/scripts/datasets/words.txt"
MUTATIONS="${BASE_DIR}/scripts/word_mutations.txt"
GROUND_FILE="${BASE_DIR}/scripts/datasets/words.txt_c_1_mutated.txt"
SCRIPT="${BASE_DIR}/scripts/parse_results.py"

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
			| xargs printf "Cluster (%d, %d): %s\n" ${clusters} ${iters}

		# print bin size and remove temp file
		rm test_${clusters}_${iters}.out
	done
done
