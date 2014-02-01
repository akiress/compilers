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

%{
private int comments = 0;
private int stringlit = 0;
private StringBuffer text = new StringBuffer();

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

whitespace=[\n\ \t\b\012]
alphabet=[a-zA-Z]
digits=[0-9]+
id=({alphabet}|{digits}|"_")*
comment=([^/*\n]|[^*\n]"/"[^*\n]|[^/\n]"*"[^/\n]|"*"[^/\n]|"/"[^*\n])*

%%
while {return tok(sym.WHILE, null);}
for {return tok(sym.FOR, null);}
to {return tok(sym.TO, null);}
break {return tok(sym.BREAK, null);}
let {return tok(sym.LET, null);}
in {return tok(sym.IN, null);}
end {return tok(sym.END, null);}
function {return tok(sym.FUNCTION, null);}
var {return tok(sym.VAR, null);}
type {return tok(sym.TYPE, null);}
array {return tok(sym.ARRAY, null);}
if {return tok(sym.IF, null);}
then {return tok(sym.THEN, null);}
else {return tok(sym.ELSE, null);}
do {return tok(sym.DO, null);}
of {return tok(sym.OF, null);}
nil {return tok(sym.NIL, null);}

" "	{}
\n	{newline();}
","	{return tok(sym.COMMA, null);}
":" {return tok(sym.COLON, null);}
";" {return tok(sym.SEMICOLON, null);}
"(" {return tok(sym.LPAREN, null);}
")" {return tok(sym.RPAREN, null);}
"[" {return tok(sym.LBRACK, null);}
"]" {return tok(sym.RBRACK, null);}
"{" {return tok(sym.LBRACE, null);}
"}" {return tok(sym.RBRACE, null);}
"." {return tok(sym.DOT, null);}
"+" {return tok(sym.PLUS, null);}
"-" {return tok(sym.MINUS, null);}
"*" {return tok(sym.TIMES, null);}
"/" {return tok(sym.DIVIDE, null);}
"=" {return tok(sym.EQ, null);}
"<>" {return tok(sym.NEQ, null);}
"<" {return tok(sym.LT, null);}
">" {return tok(sym.GT, null);}
"<=" {return tok(sym.LE, null);}
">=" {return tok(sym.GE, null);}
"&" {return tok(sym.AND, null);}
"|" {return tok(sym.OR, null);}
":=" {return tok(sym.ASSIGN, null);}

{id} {return tok(sym.ID, yytext());}
{digits} {return tok(sym.INT, Integer.parseInt(yytext()));}

"/*" {comments++; yybegin(COMMENT);}
<COMMENT>"/*" {comments++;}
<COMMENT>{comment} { }
<COMMENT>"*/" {if (--comments == 0) {yybegin(YYINITIAL); }}

. { err("Illegal character: " + yytext()); }
