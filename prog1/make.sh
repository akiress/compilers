cd Parse
java JLex.Main Tiger.lex
mv Tiger.lex.java Yylex.java
cd ..

javac -g Parse/*.java
