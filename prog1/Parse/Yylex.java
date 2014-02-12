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

private int commentDepth;
private int strings;
private StringBuffer string;
private int charPos;
private int tempCharPos;
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
private java_cup.runtime.Symbol tok(int kind, int pos, Object value) {
    return new java_cup.runtime.Symbol(kind, yychar-pos, yychar+yylength(), value);
}
private char print(String s) {
  int tmp = s.length();
  char newChar = s.charAt(tmp - 1);
  charPos = s.length() + yychar;
  System.out.println(charPos);
  return newChar;
}
private char getASCII(String s) {
  String ascii = s.substring(1, s.length());
  int i = Integer.parseInt(ascii);
  return (char)i;
}
private int getControl(String s) {
  int i = Character.getNumericValue(s.charAt(2));
  i = i - 9;
  return i;
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
		73,
		83,
		95,
		63
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
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
		YY_NO_ANCHOR,
		YY_NOT_ACCEPT,
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
		YY_NO_ANCHOR
	};
	private int yy_cmap[] = {
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 1, 2, 0, 3, 4, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 5,
		1, 0, 6, 0, 7, 0, 8, 0,
		9, 10, 11, 12, 13, 14, 15, 16,
		17, 17, 17, 17, 17, 17, 17, 17,
		17, 17, 18, 19, 20, 21, 22, 5,
		5, 23, 23, 23, 23, 23, 23, 23,
		23, 23, 23, 23, 23, 23, 23, 23,
		23, 23, 23, 23, 23, 23, 23, 23,
		23, 23, 23, 24, 25, 26, 27, 28,
		0, 29, 30, 31, 32, 33, 34, 35,
		36, 37, 35, 38, 39, 35, 40, 41,
		42, 35, 43, 44, 45, 46, 47, 48,
		35, 49, 35, 50, 1, 51, 0, 0
		
	};
	private int yy_rmap[] = {
		0, 1, 2, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 3, 4, 5,
		1, 6, 1, 7, 8, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 8, 8,
		8, 8, 8, 8, 8, 8, 8, 8,
		8, 8, 8, 8, 8, 8, 8, 1,
		9, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 10, 11, 1, 12,
		1, 1, 1, 13, 1, 1, 1, 1,
		1, 14, 15, 16, 17, 18, 19, 20,
		20, 21, 21, 22, 23, 24, 25, 19,
		26, 27, 28, 29, 30, 10, 31, 32,
		33, 21, 34, 35, 36, 37, 38, 39,
		40, 41, 42, 43, 44, 45, 46, 47,
		48, 49, 50, 51, 52, 53, 54, 55,
		56, 57, 58, 59, 60, 61, 62, 63,
		64, 65 
	};
	private int yy_nxt[][] = unpackFromString(66,52,
"1,2,3,2,3,1,4,1,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,1,22,1:2,124,125,20,75,108,109,20:2,84,20,110,111,88,20:3,90,20,112,126,20,23,24,-1:53,2,-1,2,-1:59,25,-1:57,14,-1:55,26,-1:51,27,28,-1:50,29,-1:47,20,-1:5,20,-1:4,20:22,-1:4,48,-1,48,-1:64,60,-1:35,61,-1,61,-1:48,79:2,64,80:2,79,65,79:18,74,79:26,-1:6,71,-1:45,47:2,48,47,48,47:6,76,47:4,85,47:35,66,81,97,81,66:13,86,66:7,67,66:14,68,66:4,69,66:6,-1:17,20,-1:5,20,-1:4,20:13,30,20:8,-1:18,49,-1:36,87:3,-1:2,54,-1:10,89,-1:7,55,-1,91,-1:12,56,-1:4,57,-1:6,58,78:3,58:21,-1,58:26,79:2,-1,79:3,-1,79:18,-1,79:26,70,82:3,70:21,-1,70:26,51:2,52,51:3,53,52,51:17,77,51,52,51:24,-1:17,20,-1:5,20,-1:4,20:6,31,20:5,32,20:9,-1:13,50,-1:57,99,-1:51,20,-1:5,20,-1:4,20:6,33,20:15,-1:19,93,-1:51,20,-1:5,20,-1:4,20:8,114,20:4,34,20:7,115,-1:7,59,-1:17,59:4,-1:42,20,-1:5,20,-1:4,20:4,35,20:17,-1:19,20,-1:5,20,-1:4,20:15,36,20:6,-1:2,1,61,-1,61,1:21,62,1:26,-1:17,20,-1:5,20,-1:4,20:17,37,20:4,-1:19,20,-1:5,20,-1:4,20:11,38,20:10,-1:19,72,-1:51,20,-1:5,20,-1:4,20:15,39,20:6,-1:19,20,-1:5,20,-1:4,20:5,40,20:16,-1:19,20,-1:5,20,-1:4,20:12,41,20:9,-1:19,20,-1:5,20,-1:4,20:5,42,20:16,-1:19,20,-1:5,20,-1:4,20:21,43,-1:19,20,-1:5,20,-1:4,20:10,44,20:11,-1:19,20,-1:5,20,-1:4,20:5,45,20:16,-1:19,20,-1:5,20,-1:4,20:12,46,20:9,-1:19,20,-1:5,20,-1:4,20:11,113,92,20:9,-1:19,20,-1:5,20,-1:4,20:13,94,20:4,129,20:3,-1:19,20,-1:5,20,-1:4,20:5,96,20:16,-1:19,20,-1:5,20,-1:4,20:9,98,20:12,-1:19,20,-1:5,20,-1:4,20,100,20:20,-1:19,20,-1:5,20,-1:4,20:16,101,20:5,-1:19,20,-1:5,20,-1:4,20:5,102,20:16,-1:19,20,-1:5,20,-1:4,20:14,103,20:7,-1:19,20,-1:5,20,-1:4,20,104,20:20,-1:19,20,-1:5,20,-1:4,20,105,20:20,-1:19,20,-1:5,20,-1:4,20:11,106,20:10,-1:19,20,-1:5,20,-1:4,20:13,107,20:8,-1:19,20,-1:5,20,-1:4,20:15,116,20:6,-1:19,20,-1:5,20,-1:4,20:5,117,20:16,-1:19,20,-1:5,20,-1:4,20:9,118,20:12,-1:19,20,-1:5,20,-1:4,20:9,119,20:12,-1:19,20,-1:5,20,-1:4,20:15,120,20:6,-1:19,20,-1:5,20,-1:4,20:15,121,20:6,-1:19,20,-1:5,20,-1:4,20:8,122,20:13,-1:19,20,-1:5,20,-1:4,20:17,123,20:4,-1:19,20,-1:5,20,-1:4,20:3,127,20:18,-1:19,20,-1:5,20,-1:4,20:12,128,20:9,-1:2");
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
  if (commentDepth != 0) { err("ERROR: Unclosed comment.");}
  if (strings != 0) { err("ERROR: Unclosed string.");}
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
					case 1:
						{yyline++; err("Illegal character: " + yytext()); }
					case -2:
						break;
					case 2:
						{}
					case -3:
						break;
					case 3:
						{newline();}
					case -4:
						break;
					case 4:
						{string = new StringBuffer(); strings++; yybegin(STRING);}
					case -5:
						break;
					case 5:
						{return tok(sym.AND, null);}
					case -6:
						break;
					case 6:
						{return tok(sym.LPAREN, null);}
					case -7:
						break;
					case 7:
						{return tok(sym.RPAREN, null);}
					case -8:
						break;
					case 8:
						{return tok(sym.TIMES, null);}
					case -9:
						break;
					case 9:
						{return tok(sym.PLUS, null);}
					case -10:
						break;
					case 10:
						{return tok(sym.COMMA, null);}
					case -11:
						break;
					case 11:
						{return tok(sym.MINUS, null);}
					case -12:
						break;
					case 12:
						{return tok(sym.DOT, null);}
					case -13:
						break;
					case 13:
						{return tok(sym.DIVIDE, null);}
					case -14:
						break;
					case 14:
						{return tok(sym.INT, new Integer(yytext()));}
					case -15:
						break;
					case 15:
						{return tok(sym.COLON, null);}
					case -16:
						break;
					case 16:
						{return tok(sym.SEMICOLON, null);}
					case -17:
						break;
					case 17:
						{return tok(sym.LT, null);}
					case -18:
						break;
					case 18:
						{return tok(sym.EQ, null);}
					case -19:
						break;
					case 19:
						{return tok(sym.GT, null);}
					case -20:
						break;
					case 20:
						{return tok(sym.ID, yytext());}
					case -21:
						break;
					case 21:
						{return tok(sym.LBRACK, null);}
					case -22:
						break;
					case 22:
						{return tok(sym.RBRACK, null);}
					case -23:
						break;
					case 23:
						{return tok(sym.LBRACE, null);}
					case -24:
						break;
					case 24:
						{return tok(sym.RBRACE, null);}
					case -25:
						break;
					case 25:
						{commentDepth++; yybegin(COMMENT);}
					case -26:
						break;
					case 26:
						{return tok(sym.ASSIGN, null);}
					case -27:
						break;
					case 27:
						{return tok(sym.LE, null);}
					case -28:
						break;
					case 28:
						{return tok(sym.NEQ, null);}
					case -29:
						break;
					case 29:
						{return tok(sym.GE, null);}
					case -30:
						break;
					case 30:
						{return tok(sym.DO, null);}
					case -31:
						break;
					case 31:
						{return tok(sym.IF, null);}
					case -32:
						break;
					case 32:
						{return tok(sym.IN, null);}
					case -33:
						break;
					case 33:
						{return tok(sym.OF, null);}
					case -34:
						break;
					case 34:
						{return tok(sym.TO, null);}
					case -35:
						break;
					case 35:
						{return tok(sym.END, null);}
					case -36:
						break;
					case 36:
						{return tok(sym.FOR, null);}
					case -37:
						break;
					case 37:
						{return tok(sym.LET, null);}
					case -38:
						break;
					case 38:
						{return tok(sym.NIL, null);}
					case -39:
						break;
					case 39:
						{return tok(sym.VAR, null);}
					case -40:
						break;
					case 40:
						{return tok(sym.ELSE, null);}
					case -41:
						break;
					case 41:
						{return tok(sym.THEN, null);}
					case -42:
						break;
					case 42:
						{return tok(sym.TYPE, null);}
					case -43:
						break;
					case 43:
						{return tok(sym.ARRAY, null);}
					case -44:
						break;
					case 44:
						{return tok(sym.BREAK, null);}
					case -45:
						break;
					case 45:
						{return tok(sym.WHILE, null);}
					case -46:
						break;
					case 46:
						{return tok(sym.FUNCTION, null);}
					case -47:
						break;
					case 47:
						{}
					case -48:
						break;
					case 48:
						{}
					case -49:
						break;
					case 49:
						{if (--commentDepth == 0) {yybegin(YYINITIAL); }}
					case -50:
						break;
					case 50:
						{commentDepth++;}
					case -51:
						break;
					case 51:
						{string.append(yytext()); charPos++;}
					case -52:
						break;
					case 52:
						{charPos++;}
					case -53:
						break;
					case 53:
						{yybegin(YYINITIAL); strings--; tempCharPos = 0; return tok(sym.STRING, charPos, string.toString());}
					case -54:
						break;
					case 54:
						{string.append("\"");}
					case -55:
						break;
					case 55:
						{string.append("\\");}
					case -56:
						break;
					case 56:
						{string.append("\n");}
					case -57:
						break;
					case 57:
						{string.append("\t");}
					case -58:
						break;
					case 58:
						{string.append(print(yytext()));}
					case -59:
						break;
					case 59:
						{int i = getControl(yytext()); string.append((char)i);}
					case -60:
						break;
					case 60:
						{int i = getASCII(yytext()); if (i < 256) {string.append((char)i);} else {err("ERROR: ASCII");} yybegin(STRING);}
					case -61:
						break;
					case 61:
						{}
					case -62:
						break;
					case 62:
						{yybegin(STRING);}
					case -63:
						break;
					case 63:
						{}
					case -64:
						break;
					case 64:
						{strings = 1;}
					case -65:
						break;
					case 65:
						{strings = 0;}
					case -66:
						break;
					case 66:
						{}
					case -67:
						break;
					case 67:
						{}
					case -68:
						break;
					case 68:
						{}
					case -69:
						break;
					case 69:
						{}
					case -70:
						break;
					case 70:
						{}
					case -71:
						break;
					case 71:
						{}
					case -72:
						break;
					case 72:
						{}
					case -73:
						break;
					case 74:
						{yyline++; err("Illegal character: " + yytext()); }
					case -74:
						break;
					case 75:
						{return tok(sym.ID, yytext());}
					case -75:
						break;
					case 76:
						{}
					case -76:
						break;
					case 77:
						{charPos++;}
					case -77:
						break;
					case 78:
						{string.append(print(yytext()));}
					case -78:
						break;
					case 79:
						{}
					case -79:
						break;
					case 80:
						{strings = 1;}
					case -80:
						break;
					case 81:
						{}
					case -81:
						break;
					case 82:
						{}
					case -82:
						break;
					case 84:
						{return tok(sym.ID, yytext());}
					case -83:
						break;
					case 85:
						{}
					case -84:
						break;
					case 86:
						{}
					case -85:
						break;
					case 88:
						{return tok(sym.ID, yytext());}
					case -86:
						break;
					case 90:
						{return tok(sym.ID, yytext());}
					case -87:
						break;
					case 92:
						{return tok(sym.ID, yytext());}
					case -88:
						break;
					case 94:
						{return tok(sym.ID, yytext());}
					case -89:
						break;
					case 96:
						{return tok(sym.ID, yytext());}
					case -90:
						break;
					case 98:
						{return tok(sym.ID, yytext());}
					case -91:
						break;
					case 100:
						{return tok(sym.ID, yytext());}
					case -92:
						break;
					case 101:
						{return tok(sym.ID, yytext());}
					case -93:
						break;
					case 102:
						{return tok(sym.ID, yytext());}
					case -94:
						break;
					case 103:
						{return tok(sym.ID, yytext());}
					case -95:
						break;
					case 104:
						{return tok(sym.ID, yytext());}
					case -96:
						break;
					case 105:
						{return tok(sym.ID, yytext());}
					case -97:
						break;
					case 106:
						{return tok(sym.ID, yytext());}
					case -98:
						break;
					case 107:
						{return tok(sym.ID, yytext());}
					case -99:
						break;
					case 108:
						{return tok(sym.ID, yytext());}
					case -100:
						break;
					case 109:
						{return tok(sym.ID, yytext());}
					case -101:
						break;
					case 110:
						{return tok(sym.ID, yytext());}
					case -102:
						break;
					case 111:
						{return tok(sym.ID, yytext());}
					case -103:
						break;
					case 112:
						{return tok(sym.ID, yytext());}
					case -104:
						break;
					case 113:
						{return tok(sym.ID, yytext());}
					case -105:
						break;
					case 114:
						{return tok(sym.ID, yytext());}
					case -106:
						break;
					case 115:
						{return tok(sym.ID, yytext());}
					case -107:
						break;
					case 116:
						{return tok(sym.ID, yytext());}
					case -108:
						break;
					case 117:
						{return tok(sym.ID, yytext());}
					case -109:
						break;
					case 118:
						{return tok(sym.ID, yytext());}
					case -110:
						break;
					case 119:
						{return tok(sym.ID, yytext());}
					case -111:
						break;
					case 120:
						{return tok(sym.ID, yytext());}
					case -112:
						break;
					case 121:
						{return tok(sym.ID, yytext());}
					case -113:
						break;
					case 122:
						{return tok(sym.ID, yytext());}
					case -114:
						break;
					case 123:
						{return tok(sym.ID, yytext());}
					case -115:
						break;
					case 124:
						{return tok(sym.ID, yytext());}
					case -116:
						break;
					case 125:
						{return tok(sym.ID, yytext());}
					case -117:
						break;
					case 126:
						{return tok(sym.ID, yytext());}
					case -118:
						break;
					case 127:
						{return tok(sym.ID, yytext());}
					case -119:
						break;
					case 128:
						{return tok(sym.ID, yytext());}
					case -120:
						break;
					case 129:
						{return tok(sym.ID, yytext());}
					case -121:
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
