JFLAGS=-g

Parse/*.class: Parse/*.java Parse/Yylex.java
	javac ${JFLAGS} Parse/*.java

Parse/Yylex.java: Parse/Tiger.lex
	cd Parse; java JLex.Main Tiger.lex; mv Tiger.lex.java Yylex.java

clean:
	rm -f */*.class Parse/Yylex.java
