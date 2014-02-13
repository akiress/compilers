<<<<<<< HEAD
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
=======
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

About:
      Everything other than the above listed functionality
    issue(s) should work as expected (or as demonstrated 
    via testing against the reference implementation).
      Comments, strings, and control characters are handled
    using a variety of regular expressions and starting
    characters to react as desired.
      Overall, our implementation was largely based off of
    the sample code provided on the course website (modified
    as needed) and the advice given in class and via email.

>>>>>>> db3069c2a3bd8e7d1bc98d459ed46f3b36035115
