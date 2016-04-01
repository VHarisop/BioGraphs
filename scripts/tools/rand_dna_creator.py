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
import argparse
from random import choice, randint

# set of symbols
symbols = 'ACGT'

def generate(seq_sz, num, offset, filename):
    """
    Generates [length] random strands of DNA using a given base size
    and a size offset for length variability and writes them to a file
    in FASTA format.

    :param seq_sz: the base size of each strand
    :param num: the number of random strands to be created
    :param offset: the size offset
    :param filename: the name of the file where the strands will be stored
    """

    # generator of data
    label = '>test_rand_' 

    gen_dna = lambda rng: (choice(symbols) \
            for _ in range(seq_sz + randint(-(rng / 2), rng / 2)))
    data = ((gen_dna(offset)) for _ in range(num))

    # write generated data to a file
    with open(filename, 'w') as f:
        for i, t in enumerate(data):
            f.write(label + str(i) + '\n')
            f.write(''.join(t) + '\n')

if __name__ == "__main__":
    # create a parser for cmd line args
    parser = argparse.ArgumentParser(
            description='A utility that creates random strands of DNA'
    )

    # add --number argument for the number of strands to be created
    parser.add_argument('-n', '--number',
            help = 'The number of random strands to create',
            type = int,
            nargs = '?',
            const = 1000
    )

    # add --length argument
    parser.add_argument('-l', '--length',
            help = 'The length of the DNA strand',
            type = int,
            nargs = '?',
            const = 146
    )

    # add --offset argument to create strands that differ in length
    parser.add_argument('-o', '--offset',
            help = 'The limit of the random offset of the length',
            type = int,
            nargs = '?',
            const = 10
    )

    # add --file argument for the output file
    parser.add_argument('-f', '--file',
            help = 'The file to store the generated strand into',
            nargs = '?',
            const = 'ncl.fa'
    )

    args = vars(parser.parse_args())
    generate(args['length'], args['number'], args['offset'], args['file'])
