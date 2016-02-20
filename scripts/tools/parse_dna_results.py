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
import json

def parse_json(json_string):
    """
    Given a JSON string from a test result file and a list of words that
    were tested after being mutated, outputs a list of dicts that contain
    the label of the method that was used, the method's accuracy, as well
    as a list of the method's bin sizes, if information is provided about
    them.

    :param json_string: The json string from the result file
    :return: A list of test results containing accuracy, method label, and \
             bin sizes
    """

    decoded_results = json.loads(json_string)
    result_list = []

    # anonymous function to check if a word is in a result list
    hit = lambda x: 1 if x['query'] in x['results'] else 0

    # create list of method - accuracy pairs
    for method in decoded_results:
        method_label = method['MethodLabel']
        try:
            bin_sizes = method['binSizes']
            if all(i == 1 for i in bin_sizes):
                bin_sizes = 1
        except KeyError:
            bin_sizes = 'undefined'

        hits = sum(hit(i) for i in method['ResultList'])
        accuracy = float(hits) / len(method['ResultList'])

        try:
            max_time, mean_time = method['maxTime'], method['meanTime']
        except KeyError:
            mean_time = max_time = "unmeasured"

        result_list.append({
            'method': method_label,
            'accuracy': accuracy,
            'mean_time': mean_time,
            'max_time': max_time,
            'bins': bin_sizes})

    return result_list


if __name__ == '__main__':
    # create a parser for command line arguments
    parser = argparse.ArgumentParser(
            description='Analyzes test results given a truth and a result file')

    parser.add_argument('-r', '--result_file', nargs=1,
            required=True,
            help='The file containing the test results')

    # get the 2 filenames from args
    args = vars(parser.parse_args())
    result_file = args['result_file'][0]
    
    with open(result_file, 'r') as f:
        # read all lines into file
        json_lines = ''.join(i for i in f)

        for i in parse_json(json_lines):
            time_label = 'mean: %s, max: %s' % (i['mean_time'], i['max_time'])
            print(i['method'], i['accuracy'], 'bins:', i['bins'],
                    'times: ', time_label)
