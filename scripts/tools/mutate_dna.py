#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
from Bio import SeqIO, Seq
from Bio.SeqRecord import SeqRecord
from random import choice, sample

def mutate(sequence, number):
    """
    Mutates a sequence at a specified number of random points.

    :param sequence: the sequence to mutate
    :param number: the number of mutations to apply
    :return: The mutated sequence
    """
    # the set of symbols for DNA bases
    sym_set = set(['A', 'C', 'G', 'T'])
    num = min(len(sequence), number)
    indices = sample(range(len(sequence)), num)

    for i in indices:
        # change seq[i] with a symbol in {x in sym_set: x != seq[i]}
        sequence[i] = choice(tuple(sym_set.difference({sequence[i]})))

    return sequence

if __name__ == "__main__":
    # create a parser for command line arguments
    parser = argparse.ArgumentParser(
        description='Mutates DNA strings given a fasta file'
    )

    parser.add_argument(
        '-n',
        '--number-of-mutations',
        nargs=1,
        required=True,
        type=int,
        help='The number of mutations to apply'
    )

    parser.add_argument(
        '-f',
        '--file',
        nargs=1,
        required=True,
        help='The file containing the sequences'
    )

    parser.add_argument(
        '-p',
        '--percent_change',
        help = 'The percentage of sequences that will be mutated',
        type = int,
        default = 100
    )

    args = vars(parser.parse_args())
    fasta_file = args['file'][0]
    mutation_num = args['number_of_mutations'][0]
    percent = int(round(args['percent_change'] / 100.0))

    # name the output file appropriately
    out_name = 'mutated_{0}.fasta'.format(mutation_num)

    with open(fasta_file, 'r') as f, open(out_name, 'w') as w:
        mutated_sequences = []
        fasta_sequences = list(SeqIO.parse(f, 'fasta'))

        # pick some fasta sequences at random to be mutated
        random_picks = sample(fasta_sequences, len(fasta_sequences) * percent)
        for fasta in random_picks:
            name, seq = fasta.id, mutate(fasta.seq.tomutable(), mutation_num)
            mutated_sequences.append(SeqRecord(seq, name, '', ''))

        # write mutated sequences to output file
        SeqIO.write(mutated_sequences, w, 'fasta')
