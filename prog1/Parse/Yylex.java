package Parse;
import ErrorMsg.ErrorMsg;


class Yylex implements Lexer {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final char YYEOF = '\uFFFF';

private int comments;
private int strings;
private StringBuffer string;
private int lineNum;
private int linePos;
private int charPos;
private void newline() {
  errorMsg.newline(yychar);
}
/* private void err(int pos, String s) {
  errorMsg.error(pos, s);
} */
private void err(String s) {
  err(yychar, s);
}
/* Using custom error messages instead of changing ErrorMsg.java for project 1. Will change ErrorMsg.java later */
private void err(int line, String s) {
  System.err.println("ERROR: Line " + line + " : " + s);
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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int STRING = 2;
	private final int STRING_IGNORE = 4;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int SPACE = 3;
	private final int yy_state_dtrans[] = {
		0,
		45,
		50,
		74,
		56
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private char yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YYEOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YYEOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_start () {
		if ((byte) '\n' == yy_buffer[yy_buffer_start]) {
			++yyline;
		}
		++yychar;
		++yy_buffer_start;
	}
	private void yy_pushback () {
		--yy_buffer_end;
	}
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ((byte) '\n' == yy_buffer[i]) {
				++yyline;
			}
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
private int [][] unpackFromString(int size1, int size2, String st)
    {
      int colonIndex = -1;
      String lengthString;
      int sequenceLength = 0;
      int sequenceInteger = 0;
      int commaIndex;
      String workString;
      int res[][] = new int[size1][size2];
      for (int i= 0; i < size1; i++)
	for (int j= 0; j < size2; j++)
	  {
	    if (sequenceLength == 0) 
	      {	
		commaIndex = st.indexOf(',');
		if (commaIndex == -1)
		  workString = st;
		else
		  workString = st.substring(0, commaIndex);
		st = st.substring(commaIndex+1);
		colonIndex = workString.indexOf(':');
		if (colonIndex == -1)
		  {
		    res[i][j] = Integer.parseInt(workString);
		  }
		else 
		  {
		    lengthString = workString.substring(colonIndex+1);  
		    sequenceLength = Integer.parseInt(lengthString);
		    workString = workString.substring(0,colonIndex);
		    sequenceInteger = Integer.parseInt(workString);
		    res[i][j] = sequenceInteger;
		    sequenceLength--;
		  }
	      }
	    else 
	      {
		res[i][j] = sequenceInteger;
		sequenceLength--;
	      }
	  }
      return res;
    }
	private int yy_acpt[] = {
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR
	};
	private int yy_cmap[] = {
		0, 0, 0, 0, 0, 0, 0, 0,
		1, 1, 2, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		1, 0, 3, 0, 0, 0, 4, 0,
		5, 6, 7, 8, 9, 10, 11, 12,
		13, 13, 13, 13, 13, 13, 13, 13,
		13, 13, 14, 15, 16, 17, 18, 0,
		0, 13, 13, 13, 13, 13, 13, 13,
		13, 13, 13, 13, 13, 13, 13, 13,
		13, 13, 13, 13, 13, 13, 13, 13,
		13, 13, 13, 19, 20, 21, 0, 13,
		0, 22, 23, 24, 25, 26, 27, 13,
		28, 29, 13, 30, 31, 13, 32, 33,
		34, 13, 35, 36, 37, 38, 39, 40,
		13, 41, 13, 42, 43, 44, 0, 0
		
	};
	private int yy_rmap[] = {
		0, 1, 2, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 3, 4, 1, 5,
		1, 6, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 7, 7, 7, 7,
		7, 7, 7, 7, 7, 7, 7, 7,
		7, 7, 7, 7, 7, 8, 9, 10,
		1, 1, 11, 1, 1, 12, 13, 1,
		14, 1, 1, 15, 7, 16, 17, 18,
		19, 20, 21, 22, 23, 12, 24, 25,
		26, 27, 28, 29, 30, 31, 15, 32,
		33, 34, 35, 36, 37, 38, 39, 40,
		41, 42, 43, 44, 45, 46, 47, 48,
		49, 50, 51, 52, 53, 54, 55, 56,
		57, 58, 59, 60, 61, 62, 63, 64,
		65, 66, 67, 68 
	};
	private int yy_nxt[][] = unpackFromString(69,45,
"1,2:2,3,4,5,6,7,8,9,10,11,12,60,13,14,15,16,17,18,1,19,110,111,60,66,94,95,60,72,60,96,97,77,60:3,81,60,98,112,60,20,21,22,-1:46,2:2,-1:49,23,-1:54,24,-1:44,25,26,-1:43,27,-1:40,60,-1:8,60:20,-1:3,62:2,46,62:4,61,62:4,67,62:32,-1:2,46,-1:42,62:2,-1,62:4,68,62:4,-1,62:32,64:2,51,52,64:16,70,64:24,-1,69:2,-1:17,64,-1:25,54:2,-1:42,65:2,57,58,65:16,71,65:24,-1,78:2,-1:17,65,-1:24,62:2,-1,62:4,68,62:4,48,62:34,-1,62:4,47,62:4,63,62:34,-1,62:4,-1,62:4,73,62:32,64:2,-1:2,64:16,75,64:24,65:2,-1:2,65:16,76,65:24,-1:13,60,-1:8,60:11,28,60:8,-1:3,62:2,-1,62:4,49,62:4,73,62:34,-1,62:4,68,62:4,63,62:32,64,79,53,64:17,75,64:24,65,80,59,65:17,76,65:24,-1:13,60,-1:8,60:5,29,60:4,30,60:9,-1:3,62:2,-1,62:4,47,62:4,73,62:32,1,54:2,1:17,55,1:24,64,79,69,64:17,75,64:24,65,80,78,65:17,76,65:24,-1:13,60,-1:8,60:5,31,60:14,-1:3,64,79,69,-1,64:16,75,64:24,65,80,78,-1,65:16,76,65:24,-1:13,60,-1:8,60:6,100,60:4,32,60:7,101,-1:16,60,-1:8,60:3,33,60:16,-1:16,60,-1:8,60:13,34,60:6,-1:16,60,-1:8,60:15,35,60:4,-1:16,60,-1:8,60:9,36,60:10,-1:16,60,-1:8,60:13,37,60:6,-1:16,60,-1:8,60:4,38,60:15,-1:16,60,-1:8,60:10,39,60:9,-1:16,60,-1:8,60:4,40,60:15,-1:16,60,-1:8,60:19,41,-1:16,60,-1:8,60:8,42,60:11,-1:16,60,-1:8,60:4,43,60:15,-1:16,60,-1:8,60:10,44,60:9,-1:16,60,-1:8,60:9,99,82,60:9,-1:16,60,-1:8,60:11,83,60:4,115,60:3,-1:16,60,-1:8,60:4,84,60:15,-1:16,60,-1:8,60:7,85,60:12,-1:16,60,-1:8,86,60:19,-1:16,60,-1:8,60:14,87,60:5,-1:16,60,-1:8,60:4,88,60:15,-1:16,60,-1:8,60:12,89,60:7,-1:16,60,-1:8,90,60:19,-1:16,60,-1:8,91,60:19,-1:16,60,-1:8,60:9,92,60:10,-1:16,60,-1:8,60:11,93,60:8,-1:16,60,-1:8,60:13,102,60:6,-1:16,60,-1:8,60:4,103,60:15,-1:16,60,-1:8,60:7,104,60:12,-1:16,60,-1:8,60:7,105,60:12,-1:16,60,-1:8,60:13,106,60:6,-1:16,60,-1:8,60:13,107,60:6,-1:16,60,-1:8,60:6,108,60:13,-1:16,60,-1:8,60:15,109,60:4,-1:16,60,-1:8,60:2,113,60:17,-1:16,60,-1:8,60:10,114,60:9,-1:3");
	public java_cup.runtime.Symbol nextToken ()
		throws java.io.IOException {
		char yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			if (YYEOF != yy_lookahead) {
				yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YYEOF == yy_lookahead && true == yy_initial) {

{
  return tok(sym.EOF, null);
}
				}
				else if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_to_mark();
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_pushback();
					}
					if (0 != (YY_START & yy_anchor)) {
						yy_move_start();
					}
					switch (yy_last_accept_state) {
					case 0:
						{return tok(sym.ID, yytext());}
					case -2:
						break;
					case 1:
						{lineNum = yyline + 1;err(lineNum, "Illegal character: " + yytext()); }
					case -3:
						break;
					case 2:
						{}
					case -4:
						break;
					case 3:
						{string = new StringBuffer(); strings = 1; yybegin(STRING);}
					case -5:
						break;
					case 4:
						{return tok(sym.AND, null);}
					case -6:
						break;
					case 5:
						{return tok(sym.LPAREN, null);}
					case -7:
						break;
					case 6:
						{return tok(sym.RPAREN, null);}
					case -8:
						break;
					case 7:
						{return tok(sym.TIMES, null);}
					case -9:
						break;
					case 8:
						{return tok(sym.PLUS, null);}
					case -10:
						break;
					case 9:
						{return tok(sym.COMMA, null);}
					case -11:
						break;
					case 10:
						{return tok(sym.MINUS, null);}
					case -12:
						break;
					case 11:
						{return tok(sym.DOT, null);}
					case -13:
						break;
					case 12:
						{return tok(sym.DIVIDE, null);}
					case -14:
						break;
					case 13:
						{return tok(sym.COLON, null);}
					case -15:
						break;
					case 14:
						{return tok(sym.SEMICOLON, null);}
					case -16:
						break;
					case 15:
						{return tok(sym.LT, null);}
					case -17:
						break;
					case 16:
						{return tok(sym.EQ, null);}
					case -18:
						break;
					case 17:
						{return tok(sym.GT, null);}
					case -19:
						break;
					case 18:
						{return tok(sym.LBRACK, null);}
					case -20:
						break;
					case 19:
						{return tok(sym.RBRACK, null);}
					case -21:
						break;
					case 20:
						{return tok(sym.LBRACE, null);}
					case -22:
						break;
					case 21:
						{return tok(sym.OR, null);}
					case -23:
						break;
					case 22:
						{return tok(sym.RBRACE, null);}
					case -24:
						break;
					case 23:
						{comments++; yybegin(COMMENT);}
					case -25:
						break;
					case 24:
						{return tok(sym.ASSIGN, null);}
					case -26:
						break;
					case 25:
						{return tok(sym.LE, null);}
					case -27:
						break;
					case 26:
						{return tok(sym.NEQ, null);}
					case -28:
						break;
					case 27:
						{return tok(sym.GE, null);}
					case -29:
						break;
					case 28:
						{return tok(sym.DO, null);}
					case -30:
						break;
					case 29:
						{return tok(sym.IF, null);}
					case -31:
						break;
					case 30:
						{return tok(sym.IN, null);}
					case -32:
						break;
					case 31:
						{return tok(sym.OF, null);}
					case -33:
						break;
					case 32:
						{return tok(sym.TO, null);}
					case -34:
						break;
					case 33:
						{return tok(sym.END, null);}
					case -35:
						break;
					case 34:
						{return tok(sym.FOR, null);}
					case -36:
						break;
					case 35:
						{return tok(sym.LET, null);}
					case -37:
						break;
					case 36:
						{return tok(sym.NIL, null);}
					case -38:
						break;
					case 37:
						{return tok(sym.VAR, null);}
					case -39:
						break;
					case 38:
						{return tok(sym.ELSE, null);}
					case -40:
						break;
					case 39:
						{return tok(sym.THEN, null);}
					case -41:
						break;
					case 40:
						{return tok(sym.TYPE, null);}
					case -42:
						break;
					case 41:
						{return tok(sym.ARRAY, null);}
					case -43:
						break;
					case 42:
						{return tok(sym.BREAK, null);}
					case -44:
						break;
					case 43:
						{return tok(sym.WHILE, null);}
					case -45:
						break;
					case 44:
						{return tok(sym.FUNCTION, null);}
					case -46:
						break;
					case 45:
						{}
					case -47:
						break;
					case 46:
						{}
					case -48:
						break;
					case 48:
						{if (--comments == 0) {yybegin(YYINITIAL); }}
					case -49:
						break;
					case 49:
						{comments++;}
					case -50:
						break;
					case 50:
						{string.append(yytext());}
					case -51:
						break;
					case 51:
						{lineNum = yyline + 1; err(lineNum, "Cannot have newlines in string literals."); yybegin(STRING_IGNORE);}
					case -52:
						break;
					case 52:
						{yybegin(YYINITIAL); strings = 0; return tok(sym.STRING, string.toString());}
					case -53:
						break;
					case 53:
						{yybegin(SPACE);}
					case -54:
						break;
					case 54:
						{}
					case -55:
						break;
					case 55:
						{yybegin(STRING);}
					case -56:
						break;
					case 56:
						{string.append(yytext());}
					case -57:
						break;
					case 57:
						{}
					case -58:
						break;
					case 58:
						{yybegin(YYINITIAL); strings = 0;}
					case -59:
						break;
					case 59:
						{yybegin(SPACE);}
					case -60:
						break;
					case 60:
						{return tok(sym.ID, yytext());}
					case -61:
						break;
					case 61:
						{lineNum = yyline + 1;err(lineNum, "Illegal character: " + yytext()); }
					case -62:
						break;
					case 62:
						{}
					case -63:
						break;
					case 64:
						{string.append(yytext());}
					case -64:
						break;
					case 65:
						{string.append(yytext());}
					case -65:
						break;
					case 66:
						{return tok(sym.ID, yytext());}
					case -66:
						break;
					case 67:
						{lineNum = yyline + 1;err(lineNum, "Illegal character: " + yytext()); }
					case -67:
						break;
					case 68:
						{}
					case -68:
						break;
					case 70:
						{string.append(yytext());}
					case -69:
						break;
					case 71:
						{string.append(yytext());}
					case -70:
						break;
					case 72:
						{return tok(sym.ID, yytext());}
					case -71:
						break;
					case 73:
						{}
					case -72:
						break;
					case 75:
						{string.append(yytext());}
					case -73:
						break;
					case 76:
						{string.append(yytext());}
					case -74:
						break;
					case 77:
						{return tok(sym.ID, yytext());}
					case -75:
						break;
					case 79:
						{string.append(yytext());}
					case -76:
						break;
					case 80:
						{string.append(yytext());}
					case -77:
						break;
					case 81:
						{return tok(sym.ID, yytext());}
					case -78:
						break;
					case 82:
						{return tok(sym.ID, yytext());}
					case -79:
						break;
					case 83:
						{return tok(sym.ID, yytext());}
					case -80:
						break;
					case 84:
						{return tok(sym.ID, yytext());}
					case -81:
						break;
					case 85:
						{return tok(sym.ID, yytext());}
					case -82:
						break;
					case 86:
						{return tok(sym.ID, yytext());}
					case -83:
						break;
					case 87:
						{return tok(sym.ID, yytext());}
					case -84:
						break;
					case 88:
						{return tok(sym.ID, yytext());}
					case -85:
						break;
					case 89:
						{return tok(sym.ID, yytext());}
					case -86:
						break;
					case 90:
						{return tok(sym.ID, yytext());}
					case -87:
						break;
					case 91:
						{return tok(sym.ID, yytext());}
					case -88:
						break;
					case 92:
						{return tok(sym.ID, yytext());}
					case -89:
						break;
					case 93:
						{return tok(sym.ID, yytext());}
					case -90:
						break;
					case 94:
						{return tok(sym.ID, yytext());}
					case -91:
						break;
					case 95:
						{return tok(sym.ID, yytext());}
					case -92:
						break;
					case 96:
						{return tok(sym.ID, yytext());}
					case -93:
						break;
					case 97:
						{return tok(sym.ID, yytext());}
					case -94:
						break;
					case 98:
						{return tok(sym.ID, yytext());}
					case -95:
						break;
					case 99:
						{return tok(sym.ID, yytext());}
					case -96:
						break;
					case 100:
						{return tok(sym.ID, yytext());}
					case -97:
						break;
					case 101:
						{return tok(sym.ID, yytext());}
					case -98:
						break;
					case 102:
						{return tok(sym.ID, yytext());}
					case -99:
						break;
					case 103:
						{return tok(sym.ID, yytext());}
					case -100:
						break;
					case 104:
						{return tok(sym.ID, yytext());}
					case -101:
						break;
					case 105:
						{return tok(sym.ID, yytext());}
					case -102:
						break;
					case 106:
						{return tok(sym.ID, yytext());}
					case -103:
						break;
					case 107:
						{return tok(sym.ID, yytext());}
					case -104:
						break;
					case 108:
						{return tok(sym.ID, yytext());}
					case -105:
						break;
					case 109:
						{return tok(sym.ID, yytext());}
					case -106:
						break;
					case 110:
						{return tok(sym.ID, yytext());}
					case -107:
						break;
					case 111:
						{return tok(sym.ID, yytext());}
					case -108:
						break;
					case 112:
						{return tok(sym.ID, yytext());}
					case -109:
						break;
					case 113:
						{return tok(sym.ID, yytext());}
					case -110:
						break;
					case 114:
						{return tok(sym.ID, yytext());}
					case -111:
						break;
					case 115:
						{return tok(sym.ID, yytext());}
					case -112:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
					}
				}
			}
		}
	}
}
