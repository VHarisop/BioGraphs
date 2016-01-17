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

# Returns a random set of lines from a given dictionary file

# error messages for wrong usage
usage() {
	echo -e "Usage: ./extract_words.sh -f FILE -n NUMBER"
}

not_int() {
	usage
	echo -e "\tNUMBER must be an integer"
}

# parse parameters
while [[ $# > 1 ]] 
do
	key=$1
	case $key in 
		-f|--file)
			infile="$2"
			shift ;;
		-n|--number)
			number="$2"
			shift ;;
		*)
			usage
			exit ;;
	esac
	shift
done

# check if given number is an integer 
case $number in 
	''|*[!0-9]*) 
		not_int
		exit ;;
	*) ;;
esac

# if file doesn't exist, exit gracefully
[[ ! -f $infile ]] && echo "${infile}: no such file!" && exit 

# output words to stdout
cat $infile | grep -v "'s" | shuf -n $number | sort
