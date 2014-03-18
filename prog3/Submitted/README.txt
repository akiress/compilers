   Authors: Ben Guitreau (cs435107) and Morgan Hargrove (cs435109)
      Date: 17 Mar. 2014
    Course: CSC 4351, Sec. 1
Instructor: Gerald Baumgartner
 Project 3: Semantic Analysis

How to compile:
    $ javac -g */*.java
    
How to run:
    $ java Semant.Main test.tig

About:
      We believe we were actually able to implement a fairly 
    comprehensive program. As far as we can tell (based on rather
    extensive testing against the reference implementation), our 
    code is capable of handling the expressions that are listed in
    project 2's Absyn directory (let, assign, for, if, while, 
    etc...) as well as declarations (for variables, functions, and 
    types).
      We chose to implement two additional classes (LoopSemant and
    LoopVarEntry, as was suggested by the project description), but
    we have included these classes within Semant.java (at the bottom
    of the code) to reduce the number of files, since both classes
    are rather small.
