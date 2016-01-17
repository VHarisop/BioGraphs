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

# Mutates a string by changing symbols or swapping pairs at random locations

import sys 
import argparse
from random import choice, sample

def change(word, num, sym_set):
	"""Mutates a string by changing one of its symbols at randomly selected
	   locations for a specified number of times. 

	   Keyword arguments:
	   word -- the list of symbols to be mutated
	   num -- the number of changes
	   sym_set -- the symbol set (candidate replacements' source)

	   Return:
	   a tuple containing the changed word and the number of
	   changes that produced different letters
	"""
	
	# cannot change the string more times than its length allows
	num = min(num, len(word))
	indices = sample(range(len(word)), num)

	for i in indices:
		# change word[i] with a symbol in {x in sym_set: x != word[i]}
		word[i] = choice(tuple(sym_set.difference({word[i]})))

	return (word, num)

def swap(word, num):
	"""Mutates a string by swapping pairs of its symbols at randomly selected
	   locations for a specified number of times. 

	   Keyword arguments:
	   word -- the list of symbols to be mutated
	   num -- the number of swaps

	   Return: 
	   a tuple containing the changed word and the 
	   number of swaps of nonidentical letters
	"""
	
	# shouldn't swap more than L / 2 pairs of letters
	num = min(num, len(word) // 2)

	# get 2 * num indices, zip an iterator on them to produce num pairs
	index_iter = iter(sample(range(len(word)), 2 * num))
	indices = zip(index_iter, index_iter)

	for i, j in indices:
		# swap word[i] with word[j] if they differ, else decrement #swaps
		if word[i] != word[j]:
			word[i], word[j] = word[j], word[i]
		else:
			num -= 1
	
	return (word, num)

if __name__ == "__main__":

    # create a parser for command line arguments
    parser = argparse.ArgumentParser(description='A utility that mutates strings')
    parser.add_argument('strings', nargs = '+')

    # add mutually exclusive options (change / swap)
    action = parser.add_mutually_exclusive_group(required=True)
    action.add_argument('-c', '--change',
                         action='store_true',
                         help='Change random symbols in the strings')
    action.add_argument('-s', '--swap',
                        action='store_true',
                        help='Swap pairs of symbols in the strings')

    parser.add_argument('-n', '--number', type=int, help='number of changes/swaps')
    args = vars(parser.parse_args())

    # get set of symbols in the strings
    symbol_set = set.union(*(set(i) for i in args['strings']))

    mutated_strings = []

    # mutate strings according to provided flag
    if args['change']:
        for word_syms in map(list, args['strings']):
            mutated_strings.append(change(word_syms, args['number'], symbol_set))
    elif args['swap']:
        for word_syms in map(list, args['strings']):
            mutated_strings.append(swap(word_syms, args['number']))

    # print them all to stdout
    for (word_syms, num) in mutated_strings:
        print('{0}: {1}'.format(str(num), ''.join(word_syms)))
