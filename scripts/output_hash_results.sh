#!/usr/bin/env bash

BASE_DIR="${HOME}/demokritos/BioGraphs"
MAIN_CLASS="gr.demokritos.biographs.experiments.CheckVertexHash"
WORD_FILE="${BASE_DIR}/scripts/datasets/words.txt"
MUTATION_FILE="${BASE_DIR}/scripts/word_mutations.txt"
GROUND_FILE="${BASE_DIR}/scripts/datasets/words.txt_c_1_mutated.txt"
PARSE_SCRIPT="${BASE_DIR}/scripts/parse_results.py"

# Run the experiment for all combos first, save to temporary files
for num_neighb in 1 2 3 4 5 7; do
	for num_bins in 10 12 14 16 18 20; do
		mvn exec:java \
			-Dexec.mainClass=${MAIN_CLASS} \
			-Dexec.args="${WORD_FILE} ${MUTATION_FILE} ${num_neighb} ${num_bins}" \
			| grep -v "INFO" > test_${num_neighb}_${num_bins}.out

	done
done


for num_neighb in 1 2 3 4 5 7; do
	for num_bins in 10 12 14 16 18 20; do
		python3 ${PARSE_SCRIPT} \
			-g ${GROUND_FILE} -r test_${num_neighb}_${num_bins}.out \
			| awk -F ' ' '{print $2}' \
			| xargs printf "(%d, %d): %s\n" ${num_neighb} ${num_bins}
		rm test_${num_neighb}_${num_bins}.out
	done
done


