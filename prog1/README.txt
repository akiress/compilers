   Authors: Ben Guitreau (cs435107) and Morgan Hargrove (cs435109)
      Date: 11 Feb. 2014
    Course: CSC 4351, Sec. 1
Instructor: Gerald Baumgartner
 Project 1: Lexical Analysis

How to compile:
    $ make
    
How to run:
    $ java Parse.Main inputFile.tig
        
Functionality Issues:
    - STRING (string literal) indices / character positions
      are not correct, e.g.:
        "string" 
      should return 
        STRING 0 "string"
      but is actually returning
        STRING 7 "string"
