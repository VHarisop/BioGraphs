#!/usr/bin/env python3

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

# Creates a set of (pseudo-)random DNA sequences of size equal to the size
# of a nucleosome +/- L, where L is uniformly distributed in {1, 2, .. 10}.

import sys
from random import choice, randint

# set of symbols
symbols = 'ACGT'

seq_sz = 146
length = 1000
offset = 10

# default filename to write
fname = 'ncl_rand.fa'

# if argument was provided, update length
try:
	length = sys.argv[1]
	fname = sys.argv[2]
except IndexError:
	pass

# if not integer, inform
try:
	length = int(length)
except ValueError:
	print("Usage: ./rand_dna_creator [size] [filename]")
	exit() 

# generator of data
label = '>test_rand_' 

gen_dna = lambda rng: (choice(symbols) for _ in range(seq_sz + randint(1, rng)))
data = ((gen_dna(offset)) for _ in range(length))

# write generated data to a file
with open(fname, 'w') as f:
	for i, t in enumerate(data):
		f.write(label + str(i) + '\n')
		f.write(''.join(t) + '\n')


