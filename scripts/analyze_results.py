#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <https://www.gnu.org/licenses/>.

import argparse

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

if __name__ == '__main__':
    # create a parser for command line arguments
    parser = argparse.ArgumentParser(
            description='Analyzes test results given a ground truth and a res file')

    parser.add_argument('-g', '--ground_truth_file', nargs=1,
            required=True,
            help='The file containing the desired results')
    parser.add_argument('-r', '--result_file', nargs=1,
            required=True,
            help='The file containing the test results')
    parser.add_argument('-o', '--output_file', nargs='?',
            help='An output file containing the actual values along with \
                  the values assigned to them by the test')

    # get the 2 filenames from args
    args = vars(parser.parse_args())
    result_file = args['result_file'][0]
    ground_file = args['ground_truth_file'][0]
    out_file = args['output_file']
    
    with open(result_file, 'r') as f, open(ground_file) as g:
        
        if out_file:
            out = open(out_file, 'w')

        # res_lines: [noisy_word, res_a res_b res_c ... res_n]
        res_lines = [i.strip().split(':') for i in 
                        filter(lambda x: 'INFO' not in x, [j for j in f])]

        truth_lines = [i.strip() for i in g]

        num_words = len(truth_lines)
        hits = 0

        bin_lens = []
        for (res_line, truth_word) in zip(res_lines, truth_lines):
            if out_file:
                out.write('{0} - {1}\n'.format(truth_word, res_line[1]))
            if truth_word in res_line[1].split():
                hits += 1

            bin_lens.append(len(res_line[1].split()))

    print('Accuracy: {0}'.format(hits / num_words))
    print('Bin Lengths: {0}'.format(' '.join(str(len(i[1].split())) for i in res_lines)))
