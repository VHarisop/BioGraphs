"""
read_arg_data.py: A tool for converting graphs in the ARG database[1] to JSON
format.

[1]: http://mivia.unisa.it/datasets/graph-database/arg-database/
"""
import argparse
import json
import numpy

def file_reader(filepath):
    """
    Reads a description of a graph from a binary file and converts it
    to a list of nodes and an adjacency matrix.

    Arguments
    ---------
    filepath : str
        The path of the binary file

    Returns
    -------
    nodes : list
        A list of labels corresponding to nodes
    adj_mat : dict
        A dict holding the adjacency matrix of the graph
    """
    # read the file, expecting little-endian, 16-bit elements
    data = numpy.fromfile(filepath, dtype='<i2')
    # data[0] contains the number of nodes
    nodes = ['N{}'.format(i) for i in range(data[0])]
    # adjacency matrix
    adj_mat = {node: [] for node in nodes}
    # create an iterator over the rest of the data
    it_data = iter(data[1:])
    for node in nodes:
        num_adj = next(it_data)
        if num_adj != 0:
            # consume [num_adj] next integers from iterator
            for _ in range(num_adj):
                adj_mat[node].append(nodes[next(it_data)])
    # return adjacency matrix
    return adj_mat

def extract_to_json_file(filename, adj_mat):
    """
    Extracts a graph read from a binary file to a .json file, given the
    graph's adjacency matrix.

    Arguments
    ---------
    filename : str
        The name of the json file to save the graph into
    adj_mat : dict
        The graph's adjacency matrix, holding a (possibly empty) list of
        outgoing connections for every node
    """
    # Make sure filename ends with .json
    if not filename.endswith('.json'):
        filename += '.json'
    with open(filename, 'w') as jfp:
        json.dump(adj_mat, jfp, indent=2)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Read a graph from the ARG dataset and convert it to '
            'JSON format')
    parser.add_argument('-f', '--file',
        help='The path to the file',
        type=str)
    parser.add_argument('-o', '--output_file',
        help='The path to the output file',
        type=str,
        required=True)

    args = vars(parser.parse_args())
    adj_mat = file_reader(args['file'])
    extract_to_json_file(args['output_file'], adj_mat)
