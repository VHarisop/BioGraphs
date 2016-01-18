#!/usr/bin/env bash
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

# Creates a test sequence with mutated words from an input file

# error messages 
no_file() {
	echo "You must provide a valid file as first argument!"
	exit
}

no_script() {
	echo "No string_mutator.py found in current directory!"
	exit
}

# default type is change, not swap - default number of mutations is 1
infile=$1
mut_type=${2:-c}
num=${3:-1}

# if the required script is not here, exit gracefully
[[ ! -f "string_mutator.py" ]] && no_script

# if the file has not been provided or is empty, exit gracefully
([[ -z ${infile} ]] ||  [[ ! -f $infile ]]) && no_file


# do the actual work
# turn linebreak-separated file to space separated words
words=`shuf -n 50 ${infile} | tr '\n' ' ' | cat - <(echo "")`

# feed them all to the script
python3 ./string_mutator.py -${mut_type} -n ${num} ${words}
