   Authors: Ben Guitreau (cs435107) and Morgan Hargrove (cs435109)
      Date: 24 Feb. 2014
    Course: CSC 4351, Sec. 1
Instructor: Gerald Baumgartner
 Project 2: Parsing

How to compile:
    $ make
    
How to run:
    $ java Parse.Main test.tig
        
Functionality Issue(s):
    - "in" is not parsed completely correctly; "in" 
      statements are wrapped in a ExpList inside of
      a SeqExp; e.g., our implementation returns:
        SeqExp(
          ExpList(
            VarExp(
              SimpleVar(arr1))))
      when it should return:
        VarExp(
          SimpleVar(arr1))
      We chose to leave this error because correcting
      it would involve changing the way we handle
      LET, IN, SeqExp, and / or ExpList, possibly
      introducing new shift/reduce or even reduce/reduce
      conflicts in the process.

About:
      Everything other than the above listed functionality
    issue(s) should work as expected (or as demonstrated 
    via testing against the reference implementation). Our
    implementation decisions were primarily based off of
    following the UML diagram, making minor modifications 
    where we felt they might be necessary / useful.
      We resolved parsing conflicts by changing the grammar
    to ensure that only one structure is possible (making
    sure there is no left recursion / no common left 
    factors). We implemented minimal error recovery 
    (primarily just to ensure that user input / test cases 
    are valid).
      Our grammar has no reduce/reduce conflicts, but it
    does contain two unresolved shift/reduce conflicts
    (one between Types and TypeDec and the other between
    FunDec_Rec and FunDec). These conflicts were not 
    resolved because they are not harmful (the grammar
    can still be parsed using 1-token lookahead).

