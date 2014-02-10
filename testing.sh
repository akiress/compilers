#!/bin/bash
FILES=~/git/compilers/pub/tiger/testcases
OUTPUT=~/git/compilers
INPUT=~/git/compilers/prog1
TESTOUTPUT=~/git/compilers/testing.dat


cd $OUTPUT
rm testing.dat
cd $INPUT
for f in $FILES/*.tig
do
	echo $f
	printf "%s\n" $f >> $TESTOUTPUT
	printf "%s" ----- >> $TESTOUTPUT
	printf "\n" >> $TESTOUTPUT
	java Parse.Main $f >> $TESTOUTPUT
	printf "\n" >> $TESTOUTPUT
done
cd $OUTPUT
