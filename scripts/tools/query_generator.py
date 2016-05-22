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

# Creates a set of query DNA sequences of specified length from a given
# file in FASTA format. Every query sequence shares its label with the
# sequence it was generated from.

import sys
import argparse, sys, random
from Bio import SeqIO, Seq
from Bio.SeqRecord import SeqRecord

def parse_sequence(fasta_seq, length):
    """
    Parses a FASTA sequence from the original set and creates a query
    subsequence that is a slice of the original with a given length.

    Args:
        fasta_seq (Bio.Seq) -- the original fasta sequence
        length (int) -- the length of the generated query sequence

    Returns:
        Bio.Seq -- the generated query sequence
    """
    # extract name and sequence
    name, seq = fasta_seq.id, fasta_seq.seq

    # Cannot create a larger sequence than the original
    length = min(length, len(seq))

    # find the maximum starting index that generates a full
    # length subsequence
    max_start = len(seq) - length
    start = random.randint(0, max_start)
    
    # generate a new sequence
    gen_seq = seq[start:(start + length)]

    # return the generated sequence
    return SeqRecord(gen_seq, name, '', '')

if __name__ == "__main__":
    # create a parser for cmd line args
    parser = argparse.ArgumentParser(
            description='A utility that generates query DNA sequences \
                from a set of sequences in a FASTA file.'
    )

    # add --number argument for the number of strands to be created
    parser.add_argument('-n', '--number',
            help = 'The number of query sequences to create',
            type = int,
            default = 100
    )

    # add --length argument
    parser.add_argument('-l', '--length',
            help = 'The length of each query sequence',
            type = int,
            default = 1000
    )

    parser.add_argument('-i', '--input',
            help = 'The path to the file containing the original set',
            type = str,
            default = 'ecoli.nt'
    )

    # add --file argument for the output file
    parser.add_argument('-f', '--file',
            help = 'The file to store the query sequences in',
            type = str,
            default = 'query.fa'
    )

    args = vars(parser.parse_args())
    # args['length'], args['number'], args['file'], args['input']

    with open(args['input'], 'r') as infile, open(args['file'], 'w') as w:
        fasta_sequences = list(SeqIO.parse(infile, 'fasta'))
        query_sequences = []

        # pick a random sample of the sequences
        random_picks = random.sample(fasta_sequences, args['number'])

        # create a query subsequence for every original
        for fasta in random_picks:
            query_sequences.append(parse_sequence(fasta, args['length']))

        # write all query sequences to output file
        SeqIO.write(query_sequences, w, 'fasta')
