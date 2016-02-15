#!/usr/bin/env bash

BASE_DIR="${HOME}/demokritos/BioGraphs"
MAIN="gr.demokritos.biographs.experiments.TestTree"
WORDS="${BASE_DIR}/scripts/datasets/words.txt"
MUTATIONS="${BASE_DIR}/scripts/word_mutations.txt"
GROUND_FILE="${BASE_DIR}/scripts/datasets/words.txt_c_1_mutated.txt"
SCRIPT="${BASE_DIR}/scripts/parse_results.py"

# run a clean compilation
mvn clean compile
[[ $? -ne 0 ]] && exit

# Run the experiment for all combos
for branches in 10 8 7 6 5 4 3; do
	for bins in 26 24 20 18 16 13 10; do
		mvn exec:java \
			-Dexec.mainClass=${MAIN} \
			-Dexec.args="${WORDS} ${MUTATIONS} ${bins} ${branches}" \
			| grep -v -e "INFO" -e "WARNING" > test_${branches}_${bins}.out
		python3 ${SCRIPT} \
			-g ${GROUND_FILE} -r test_${branches}_${bins}.out \
			| awk -F ' ' '{print $2}' \
			| xargs printf "Ntree(%d, %d): %s\n" ${branches} ${bins}
		
		# remove temp file
		rm test_${branches}_${bins}.out
	done
done
