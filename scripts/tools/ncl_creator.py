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

# Creates a set of (pseudo-)random DNA sequences of size similar to the
# size of a nucleosome (~ 146 bp).

import sys
from random import choice

# set of symbols
symbols = 'ACGT'

sequence_size = 146
length = 1000

# default filename to write
fname = 'ncl.fa'

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
	print("Usage: ./ncl_creator [size] [filename]")
	exit() 

# generator of data
label = '>test_' 
data = ((choice(symbols) for _ in range(sequence_size)) for _ in range(length))

# write generated data to a file
with open(fname, 'w') as f:
	for i, t in enumerate(data):
		f.write(label + str(i) + '\n')
		f.write(''.join(t) + '\n')


