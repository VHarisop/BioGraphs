#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

# minimumEditDistance is taken from:
# http://rosettacode.org/wiki/Levenshtein_distance#Python
def minimumEditDistance(s1,s2):
    if len(s1) > len(s2):
        s1, s2 = s2, s1
    distances = range(len(s1) + 1)
    for index2, char2 in enumerate(s2):
        newDistances = [index2+1]
        for index1, char1 in enumerate(s1):
            if char1 == char2:
                newDistances.append(distances[index1])
            else:
                newDistances.append(1 + min((distances[index1],
                                             distances[index1 + 1],
                                             newDistances[-1])))
        distances = newDistances
    return distances[-1]

try:
    result_file = sys.argv[1]
    ground_file = sys.argv[2]
except IndexError:
    print('Usage: python3 analyze_results.py <result_file> <ground_file>')
    exit()

with open(result_file, 'r') as f, open(ground_file) as g:
    # res_lines: [noisy_word, res_a res_b res_c ... res_n]
    res_lines = [i.strip().split(':') for i in 
                    filter(lambda x: 'INFO' not in x, [j for j in f])]

    truth_lines = [i.strip() for i in g]

    num_words = len(truth_lines)
    hits = 0

    for (res_line, truth_word) in zip(res_lines, truth_lines):
        print('{0} - {1}'.format(truth_word, res_line[1]))
        if truth_word in res_line[1].split():
            hits += 1

print('Accuracy: {0}'.format(hits / num_words))
