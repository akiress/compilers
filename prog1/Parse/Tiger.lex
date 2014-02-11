package Parse;
import ErrorMsg.ErrorMsg;

%% 

%implements Lexer
%function nextToken
%type java_cup.runtime.Symbol
%char
%line
%state COMMENT
%state STRING
%state SPACE
%state STRING_IGNORE

%{
private int commentDepth;
private int strings;
private StringBuffer string;

private void newline() {
  errorMsg.newline(yychar);
}

private void err(int pos, String s) {
  errorMsg.error(pos, s);
}

private void err(String s) {
  err(yychar, s);
}

private java_cup.runtime.Symbol tok(int kind) {
    return tok(kind, null);
}

private java_cup.runtime.Symbol tok(int kind, Object value) {
    return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

private String print(String s) {
  String newStr = s.substring(1, s.length());
  return newStr;
}

private char getASCII(String s) {
  String ascii = s.substring(1, s.length());
  int i = Integer.parseInt(ascii);
  return (char)i;
}

private ErrorMsg errorMsg;

Yylex(java.io.InputStream s, ErrorMsg e) {
  this(s);
  errorMsg=e;
}

%}

%eofval{
{
  if (commentDepth != 0) { err("ERROR: Unclosed comment.");}
  if (strings != 0) { err("ERROR: Unclosed string.");}
  return tok(sym.EOF, null);
}
%eofval}       

%%
<YYINITIAL>[" "|\t|\f]+ {}
<YYINITIAL>[\n\r] {newline();}

<YYINITIAL>while {return tok(sym.WHILE, null);}
<YYINITIAL>for {return tok(sym.FOR, null);}
<YYINITIAL>to {return tok(sym.TO, null);}
<YYINITIAL>break {return tok(sym.BREAK, null);}
<YYINITIAL>let {return tok(sym.LET, null);}
<YYINITIAL>in {return tok(sym.IN, null);}
<YYINITIAL>end {return tok(sym.END, null);}
<YYINITIAL>function {return tok(sym.FUNCTION, null);}
<YYINITIAL>var {return tok(sym.VAR, null);}
<YYINITIAL>type {return tok(sym.TYPE, null);}
<YYINITIAL>array {return tok(sym.ARRAY, null);}
<YYINITIAL>if {return tok(sym.IF, null);}
<YYINITIAL>then {return tok(sym.THEN, null);}
<YYINITIAL>else {return tok(sym.ELSE, null);}
<YYINITIAL>do {return tok(sym.DO, null);}
<YYINITIAL>of {return tok(sym.OF, null);}
<YYINITIAL>nil {return tok(sym.NIL, null);}

<YYINITIAL>"," {return tok(sym.COMMA, null);}
<YYINITIAL>":" {return tok(sym.COLON, null);}
<YYINITIAL>";" {return tok(sym.SEMICOLON, null);}

<YYINITIAL>"(" {return tok(sym.LPAREN, null);}
<YYINITIAL>")" {return tok(sym.RPAREN, null);}
<YYINITIAL>"[" {return tok(sym.LBRACK, null);}
<YYINITIAL>"]" {return tok(sym.RBRACK, null);}
<YYINITIAL>"{" {return tok(sym.LBRACE, null);}
<YYINITIAL>"}" {return tok(sym.RBRACE, null);}

<YYINITIAL>"." {return tok(sym.DOT, null);}
<YYINITIAL>"+" {return tok(sym.PLUS, null);}
<YYINITIAL>"-" {return tok(sym.MINUS, null);}
<YYINITIAL>"*" {return tok(sym.TIMES, null);}
<YYINITIAL>"/" {return tok(sym.DIVIDE, null);}

<YYINITIAL>"=" {return tok(sym.EQ, null);}
<YYINITIAL>"<>" {return tok(sym.NEQ, null);}
<YYINITIAL>"<" {return tok(sym.LT, null);}
<YYINITIAL>">" {return tok(sym.GT, null);}
<YYINITIAL>"<=" {return tok(sym.LE, null);}
<YYINITIAL>">=" {return tok(sym.GE, null);}
<YYINITIAL>"&" {return tok(sym.AND, null);}
<YYINITIAL>"|" {return tok(sym.OR, null);}
<YYINITIAL>":=" {return tok(sym.ASSIGN, null);}

<YYINITIAL>[a-zA-Z][a-zA-Z0-9_]* {return tok(sym.ID, yytext());}
<YYINITIAL>[0-9]+ {return tok(sym.INT, new Integer(yytext()));}

<YYINITIAL>"\"" {string = new StringBuffer(); strings++; yybegin(STRING);}
<STRING>[\n\r] {yyline++; err("Cannot have newlines in string literals. Use '\\' to continue to another line."); yybegin(STRING_IGNORE);}
<STRING>\\n {string.append("\n");}
<STRING>\\t {string.append("\t");}
<STRING>"\\\"" {string.append("\"");}
<STRING>\\\\ {string.append("\\");}
<STRING>\\[\n|\t|\ |\f]+[^\\] {string.append(print(yytext()));}
<STRING>\\[0-9][0-9][0-9]+ {int i = getASCII(yytext()); System.out.println(i); if (i < 256) {string.append((char)i);} else {err("ERROR: ASCII");} yybegin(STRING);}
<STRING>"\"" {yybegin(YYINITIAL); strings--; return tok(sym.STRING, string.toString());}
<STRING>. {string.append(yytext());}

<STRING_IGNORE>[\n\r\f] {strings = 1;}
<STRING_IGNORE>[^\"\\\n]* {}
<STRING_IGNORE>\\n {}
<STRING_IGNORE>\\t {}
<STRING_IGNORE>"\\\"" {}
<STRING_IGNORE>\\\\ {}
<STRING_IGNORE>\\[\n|\t|\ |\f]+[^\\] {}
<STRING_IGNORE>\\[0-9][0-9][0-9] {}
<STRING_IGNORE>"\"" {strings = 0;}
<STRING_IGNORE>\\. {string.append(yytext());}

<YYINITIAL>"/*" {commentDepth++; yybegin(COMMENT);}
<COMMENT>"/*" {commentDepth++;}
<COMMENT>[\n\r]+ {}
<COMMENT>"*/" {if (--commentDepth == 0) {yybegin(YYINITIAL); }}
<COMMENT>. {}

<SPACE>[" "|\t|\f]+ {}
<SPACE>\\ {yybegin(STRING);}

. {yyline++; err("Illegal character: " + yytext()); }
