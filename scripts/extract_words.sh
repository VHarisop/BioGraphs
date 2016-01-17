#!/usr/bin/env bash

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
	''|*[!0-9]*) not_int ;; 
	*) ;;
esac

# output words to stdout
[[ -f $infile ]] && cat $infile | grep -v "'s" | shuf -n $number | sort
