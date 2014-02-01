#!/bin/bash
PARSE=~/git/compilers/prog1/Parse
PARENT=~/git/compilers/prog1

cd $PARSE
java JLex.Main Tiger.lex
mv Tiger.lex.java Yylex.java
cd $PARENT

javac -g Parse/*.java
