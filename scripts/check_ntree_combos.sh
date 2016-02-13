#!/usr/bin/env bash

BASE_DIR="${HOME}/demokritos/BioGraphs"
MAIN="gr.demokritos.biographs.experiments.TestTree"
WORDS="${BASE_DIR}/scripts/datasets/words.txt"
MUTATIONS="${BASE_DIR}/scripts/word_mutations.txt"
GROUND_FILE="${BASE_DIR}/scripts/datasets/words.txt_c_1_mutated.txt"
SCRIPT="${BASE_DIR}/scripts/parse_results.py"

# Run the experiment for all combos first, save to temporary files
for branches in 2 3 4 5 6 7 8 10; do
	for bins in 10 13 16 19 22; do
		mvn exec:java \
			-Dexec.mainClass=${MAIN} \
			-Dexec.args="${WORDS} ${MUTATIONS} ${bins} ${branches}" \
			| grep -v "INFO" > test_${branches}_${bins}.out

	done
done

# For all combos, parse the results and remove temp files
for branches in 2 3 4 5 6 7 8 10; do
	for bins in 10 13 16 19 22; do
		python3 ${SCRIPT} \
			-g ${GROUND_FILE} -r test_${branches}_${bins}.out \
			| awk -F ' ' '{print $2}' \
			| xargs printf "Ntree(%d, %d): %s\n" ${branches} ${bins}
		rm test_${branches}_${bins}.out
	done
done


