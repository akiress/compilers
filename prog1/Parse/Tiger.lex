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

%{
private int comments;
private int strings;
private StringBuffer string;

private void newline() {
  errorMsg.newline(yychar);
}

private void err(int pos, String s) {
  errorMsg.error(pos,s);
}

private void err(String s) {
  err(yychar,s);
}

private java_cup.runtime.Symbol tok(int kind) {
    return tok(kind, null);
}

private java_cup.runtime.Symbol tok(int kind, Object value) {
    return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

private ErrorMsg errorMsg;

Yylex(java.io.InputStream s, ErrorMsg e) {
  this(s);
  errorMsg=e;
}

%}

%eofval{
	{
	 return tok(sym.EOF, null);
        }
%eofval}       

alphabet=[a-zA-Z]
digits=[0-9]
nonnewline_white_space=[\ \t\b\012]
whitespace=[\n\ \t\b\012]
string_text=(\\\"|[^\n\"]|\\{whitespace}+\\)*
comment_text=([^/*\n]|[^*\n]"/"[^*\n]|[^/\n]"*"[^/\n]|"*"[^/\n]|"/"[^*\n])*
id=({alphabet}|{digits}|"_")*

%%
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

<YYINITIAL>{nonnewline_white_space}+ {}
<YYINITIAL>\n	{newline();}

<YYINITIAL>","	{return tok(sym.COMMA, null);}
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

<YYINITIAL>{id} {return tok(sym.ID, yytext());}
<YYINITIAL>{digits}+ {return tok(sym.INT, Integer.parseInt(yytext()));}

<YYINITIAL>"\"" {string = new StringBuffer(); strings = 1; yybegin(STRING);}
<STRING>{string_text} {string.append(yytext());}
<STRING>\\\"|\\\\ {string.append(yytext().charAt(1));}
<STRING>"\n" {string.append("\n");}
<STRING>"\t" {string.append("\t");}
<STRING>"\^"[a-z] {string.append((char) (yytext().charAt(2) - 'a' + 1));}
<STRING>"\"" {yybegin(YYINITIAL); strings = 0; return tok(sym.STRING, string.toString());}
<STRING>\\{whitespace} {yybegin(SPACE);}
<STRING>\\"\n" {newline(); yybegin(SPACE);}

<YYINITIAL>"/*" {comments++; yybegin(COMMENT);}
<COMMENT>"/*" {comments++;}
<COMMENT>{comment_text} {}
<COMMENT>"*/" {if (--comments == 0) {yybegin(YYINITIAL); }}

<SPACE>{whitespace}+ {}
<SPACE>\\ {yybegin(STRING);}
<SPACE>. { err("Illegal character: " + yytext()); }
