#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
parse_sdf.py: Parse a structured .mdf file, extracting info about
individual atoms and bonds.
"""

from __future__ import print_function
import json
import sys
from collections import defaultdict

def extract_sdf_sections(filename):
    """
    Extracts the '$$$$'-delimited sections of the sdf file
    into a dict of lists of strings. The keys of the dict are
    the names of the molecules (not necessarily informative).

    Arguments
    ---------
    filename : str
        The name of the .sdf file

    Returns
    -------
    dict
        A dict containing the individual sections of the .sdf
        file

    Raises
    ------
    IOError
        If the provided filename does not designate an existing
        file
    """
    def consume_section(fd):
        lines = []
        while True:
            # if we reached end of file, StopIteration
            # will be raised.
            line = fd.next()
            if line.startswith("$$$$"):
                return lines
            else:
                lines.append(line)

    sdf_fd = open(filename, 'r')
    sections = {}  # dict containing the sections
    while True:
        try:
            section = consume_section(sdf_fd)
            # section[0] contains the header / structure name
            sections[section[0]] = section
        except StopIteration:  # raised by consume_section at EOF
            break

    return sections


def parse_section(sec):
    """
    Parses a section into a set of nodes and weighted edges between nodes.
    Since position info is not to be preserved, nodes are labelled using the
    convention <label>$<id>, to distinguish between identical atoms that are
    in different positions.

    Arguments
    ---------
    sec : str list
        A list of strings matching the structure of an .mdf file

    Returns
    -------
    str
        A json string containing nodes and weighted edges between nodes
    """
    # get the number of nodes and edges
    num_nodes, num_edges = [int(i) for i in sec[3].split()[:2]]

    # get the table containing positioning information to extract
    # individual node labels
    position_table = sec[4:(4 + num_nodes)]
    # create labels and keep a dict for edges
    id_dict = {}
    for index, line in enumerate(position_table):
        content = line.split()
        try:
            id_dict[index + 1] = content[3] + "$" + str(index + 1)
        except IndexError:
            print('Index Error', content, num_nodes)

    # get edge_table
    edge_table = sec[(4 + num_nodes) : (4 + num_nodes + num_edges)]
    conns = defaultdict(dict)
    for line in edge_table:
        try:
            line_info = line[:9]  # 3 characters per field
            # relevant info: (from, to, num_bonds[=weight])
            # FIXME: does the standard force 3 characters per field ?
            e_from = int(line_info[:3])
            e_to = int(line_info[3:6])
            e_weight = int(line_info[6:9])
            conns[e_from][e_to] = e_weight
        except Exception:
            print(sec[0], line)

    # sec[0] -> structure label
    return {"id": sec[0], "nodes": id_dict, "edges": conns}


def sdf_to_graph(filename, out_file):
    """
    Extracts connectivity info (nodes/atoms, edges/bonds) from an .sdf
    file and extracts that info about all sections in a .json file.

    Arguments
    ---------
    filename : str
        The name of the .sdf file
    out_file : str
        The name of the output file. If this name does not end with .json,
        it is automatically appended
    """
    # append ending if not already there
    if not out_file.endswith('.json'):
        out_file = out_file + '.json'
    sec_list = [
        parse_section(value) for value in \
        extract_sdf_sections(filename).values() \
        if parse_section(value) is not None]

    with open(out_file, 'w') as fd_dump:
        json.dump(sec_list, fd_dump, indent=2)

