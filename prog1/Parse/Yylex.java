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
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int yy_state_dtrans[] = {
		0,
		109,
		0
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
		0, 0, 1, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0,
		2, 0, 0, 0, 0, 0, 3, 0,
		4, 5, 6, 7, 8, 9, 10, 11,
		12, 12, 12, 12, 12, 12, 12, 12,
		12, 12, 13, 14, 15, 16, 17, 0,
		0, 12, 12, 12, 12, 12, 12, 12,
		12, 12, 12, 12, 12, 12, 12, 12,
		12, 12, 12, 12, 12, 12, 12, 12,
		12, 12, 12, 18, 0, 19, 0, 12,
		0, 20, 21, 22, 23, 24, 25, 12,
		26, 27, 12, 28, 29, 12, 30, 31,
		32, 12, 33, 34, 35, 36, 37, 38,
		12, 39, 12, 40, 41, 42, 0, 0
		
	};
	private int yy_rmap[] = {
		0, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 2, 3, 1, 4,
		1, 5, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 6, 6, 6, 6,
		6, 6, 6, 6, 6, 6, 6, 6,
		6, 6, 6, 6, 6, 7, 8, 1,
		6, 7, 7, 7, 7, 9, 7, 7,
		7, 7, 10, 11, 7, 12, 7, 13,
		7, 7, 7, 7, 7, 7, 7, 7,
		7, 14, 14, 14, 14, 14, 14, 14,
		14, 14, 14, 14, 14, 14, 14, 14,
		14, 14, 15, 16, 17, 18, 19, 20,
		21, 22, 23, 24, 25, 26, 27, 28,
		29, 30, 31, 32, 33, 34, 14, 35,
		36, 37, 38, 39, 40, 41, 42, 43,
		44, 45, 46, 47, 48, 49, 50, 51,
		52, 53, 54, 55, 56, 57, 58, 59,
		60, 61, 62, 63, 64, 65, 66, 67,
		68, 69, 70, 71, 72, 73, 74, 75,
		76, 77, 78, 79, 80, 81, 82, 83,
		84, 85, 86, 87, 88, 89, 90, 91,
		92, 93, 94 
	};
	private int yy_nxt[][] = unpackFromString(95,43,
"1,2,3,4,5,6,7,8,9,10,11,12,48,13,14,15,16,17,18,19,159,161,48,92,127,129,48,94,48,131,133,95,48:3,96,48,135,163,48,20,21,22,-1:49,23,-1:52,24,-1:42,25,26,-1:41,27,-1:38,48,-1:7,48:20,-1:3,45,-1,45:4,46,45:4,91,45:32,-1,45:4,90,45:4,-1,45:32,-1,45:4,90,45:4,47,45:32,-1,45:4,23,45:4,93,45:32,-1,45:4,46,45:4,91,45:4,69,45:27,-1,45:4,46,45:4,91,45:4,70,71,45:26,-1,45:4,46,45:4,91,45:4,72,45:27,-1,45:4,46,45:4,91,110,45:7,110:20,45:4,-1,45:4,90,45:4,91,45:32,-1,45:4,-1,45:4,93,45:31,-1:12,48,-1:7,48:11,28,48:8,-1:3,45,-1,45:4,46,45:4,93,45:31,-1:12,48,-1:7,48:5,29,48:4,30,48:9,-1:15,48,-1:7,48:5,31,48:14,-1:15,48,-1:7,48:6,139,48:4,32,48:7,141,-1:15,48,-1:7,48:3,33,48:16,-1:15,48,-1:7,48:13,34,48:6,-1:15,48,-1:7,48:15,35,48:4,-1:15,48,-1:7,48:9,36,48:10,-1:15,48,-1:7,48:13,37,48:6,-1:15,48,-1:7,48:4,38,48:15,-1:15,48,-1:7,48:10,39,48:9,-1:15,48,-1:7,48:4,40,48:15,-1:15,48,-1:7,48:19,41,-1:15,48,-1:7,48:8,42,48:11,-1:15,48,-1:7,48:4,43,48:15,-1:15,48,-1:7,48:10,44,48:9,-1:3,45,2,49,50,51,52,53,54,55,56,57,58,110,59,60,61,62,63,64,65,160,162,110,111,128,130,110,112,110,132,134,113,110:3,114,110,136,164,110,66,67,68,45,-1,45:4,46,45:4,91,110,45:7,110:11,73,110:8,45:4,-1,45:4,46,45:4,91,110,45:7,110:5,74,110:4,75,110:9,45:4,-1,45:4,46,45:4,91,110,45:7,110:5,76,110:14,45:4,-1,45:4,46,45:4,91,110,45:7,110:6,140,110:4,77,110:7,142,45:4,-1,45:4,46,45:4,91,110,45:7,110:3,78,110:16,45:4,-1,45:4,46,45:4,91,110,45:7,110:13,79,110:6,45:4,-1,45:4,46,45:4,91,110,45:7,110:15,80,110:4,45:4,-1,45:4,46,45:4,91,110,45:7,110:9,81,110:10,45:4,-1,45:4,46,45:4,91,110,45:7,110:13,82,110:6,45:4,-1,45:4,46,45:4,91,110,45:7,110:4,83,110:15,45:4,-1,45:4,46,45:4,91,110,45:7,110:10,84,110:9,45:4,-1,45:4,46,45:4,91,110,45:7,110:4,85,110:15,45:4,-1,45:4,46,45:4,91,110,45:7,110:19,86,45:4,-1,45:4,46,45:4,91,110,45:7,110:8,87,110:11,45:4,-1,45:4,46,45:4,91,110,45:7,110:4,88,110:15,45:4,-1,45:4,46,45:4,91,110,45:7,110:10,89,110:9,45:3,-1:12,48,-1:7,48:9,137,97,48:9,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:9,138,115,110:9,45:3,-1:12,48,-1:7,48:11,98,48:4,169,48:3,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:11,116,110:4,170,110:3,45:3,-1:12,48,-1:7,48:4,99,48:15,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:4,117,110:15,45:3,-1:12,48,-1:7,48:7,100,48:12,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:7,118,110:12,45:3,-1:12,48,-1:7,101,48:19,-1:3,45,-1,45:4,46,45:4,91,110,45:7,119,110:19,45:3,-1:12,48,-1:7,48:14,102,48:5,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:14,120,110:5,45:3,-1:12,48,-1:7,48:4,103,48:15,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:4,121,110:15,45:3,-1:12,48,-1:7,48:12,104,48:7,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:12,122,110:7,45:3,-1:12,48,-1:7,105,48:19,-1:3,45,-1,45:4,46,45:4,91,110,45:7,123,110:19,45:3,-1:12,48,-1:7,106,48:19,-1:3,45,-1,45:4,46,45:4,91,110,45:7,124,110:19,45:3,-1:12,48,-1:7,48:9,107,48:10,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:9,125,110:10,45:3,-1:12,48,-1:7,48:11,108,48:8,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:11,126,110:8,45:3,-1:12,48,-1:7,48:13,143,48:6,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:13,144,110:6,45:3,-1:12,48,-1:7,48:4,145,48:15,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:4,146,110:15,45:3,-1:12,48,-1:7,48:7,147,48:12,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:7,148,110:12,45:3,-1:12,48,-1:7,48:7,149,48:12,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:7,150,110:12,45:3,-1:12,48,-1:7,48:13,151,48:6,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:13,152,110:6,45:3,-1:12,48,-1:7,48:13,153,48:6,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:13,154,110:6,45:3,-1:12,48,-1:7,48:6,155,48:13,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:6,156,110:13,45:3,-1:12,48,-1:7,48:15,157,48:4,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:15,158,110:4,45:3,-1:12,48,-1:7,48:2,165,48:17,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:2,166,110:17,45:3,-1:12,48,-1:7,48:10,167,48:9,-1:3,45,-1,45:4,46,45:4,91,110,45:7,110:10,168,110:9,45:3");
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
						{ err("Illegal character: " + yytext()); }
					case -3:
						break;
					case 2:
						{newline();}
					case -4:
						break;
					case 3:
						{}
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
						{ }
					case -47:
						break;
					case 47:
						{if (--comments == 0) {yybegin(YYINITIAL); }}
					case -48:
						break;
					case 48:
						{return tok(sym.ID, yytext());}
					case -49:
						break;
					case 49:
						{}
					case -50:
						break;
					case 50:
						{return tok(sym.AND, null);}
					case -51:
						break;
					case 51:
						{return tok(sym.LPAREN, null);}
					case -52:
						break;
					case 52:
						{return tok(sym.RPAREN, null);}
					case -53:
						break;
					case 53:
						{return tok(sym.TIMES, null);}
					case -54:
						break;
					case 54:
						{return tok(sym.PLUS, null);}
					case -55:
						break;
					case 55:
						{return tok(sym.COMMA, null);}
					case -56:
						break;
					case 56:
						{return tok(sym.MINUS, null);}
					case -57:
						break;
					case 57:
						{return tok(sym.DOT, null);}
					case -58:
						break;
					case 58:
						{return tok(sym.DIVIDE, null);}
					case -59:
						break;
					case 59:
						{return tok(sym.COLON, null);}
					case -60:
						break;
					case 60:
						{return tok(sym.SEMICOLON, null);}
					case -61:
						break;
					case 61:
						{return tok(sym.LT, null);}
					case -62:
						break;
					case 62:
						{return tok(sym.EQ, null);}
					case -63:
						break;
					case 63:
						{return tok(sym.GT, null);}
					case -64:
						break;
					case 64:
						{return tok(sym.LBRACK, null);}
					case -65:
						break;
					case 65:
						{return tok(sym.RBRACK, null);}
					case -66:
						break;
					case 66:
						{return tok(sym.LBRACE, null);}
					case -67:
						break;
					case 67:
						{return tok(sym.OR, null);}
					case -68:
						break;
					case 68:
						{return tok(sym.RBRACE, null);}
					case -69:
						break;
					case 69:
						{return tok(sym.ASSIGN, null);}
					case -70:
						break;
					case 70:
						{return tok(sym.LE, null);}
					case -71:
						break;
					case 71:
						{return tok(sym.NEQ, null);}
					case -72:
						break;
					case 72:
						{return tok(sym.GE, null);}
					case -73:
						break;
					case 73:
						{return tok(sym.DO, null);}
					case -74:
						break;
					case 74:
						{return tok(sym.IF, null);}
					case -75:
						break;
					case 75:
						{return tok(sym.IN, null);}
					case -76:
						break;
					case 76:
						{return tok(sym.OF, null);}
					case -77:
						break;
					case 77:
						{return tok(sym.TO, null);}
					case -78:
						break;
					case 78:
						{return tok(sym.END, null);}
					case -79:
						break;
					case 79:
						{return tok(sym.FOR, null);}
					case -80:
						break;
					case 80:
						{return tok(sym.LET, null);}
					case -81:
						break;
					case 81:
						{return tok(sym.NIL, null);}
					case -82:
						break;
					case 82:
						{return tok(sym.VAR, null);}
					case -83:
						break;
					case 83:
						{return tok(sym.ELSE, null);}
					case -84:
						break;
					case 84:
						{return tok(sym.THEN, null);}
					case -85:
						break;
					case 85:
						{return tok(sym.TYPE, null);}
					case -86:
						break;
					case 86:
						{return tok(sym.ARRAY, null);}
					case -87:
						break;
					case 87:
						{return tok(sym.BREAK, null);}
					case -88:
						break;
					case 88:
						{return tok(sym.WHILE, null);}
					case -89:
						break;
					case 89:
						{return tok(sym.FUNCTION, null);}
					case -90:
						break;
					case 90:
						{ }
					case -91:
						break;
					case 92:
						{return tok(sym.ID, yytext());}
					case -92:
						break;
					case 93:
						{ }
					case -93:
						break;
					case 94:
						{return tok(sym.ID, yytext());}
					case -94:
						break;
					case 95:
						{return tok(sym.ID, yytext());}
					case -95:
						break;
					case 96:
						{return tok(sym.ID, yytext());}
					case -96:
						break;
					case 97:
						{return tok(sym.ID, yytext());}
					case -97:
						break;
					case 98:
						{return tok(sym.ID, yytext());}
					case -98:
						break;
					case 99:
						{return tok(sym.ID, yytext());}
					case -99:
						break;
					case 100:
						{return tok(sym.ID, yytext());}
					case -100:
						break;
					case 101:
						{return tok(sym.ID, yytext());}
					case -101:
						break;
					case 102:
						{return tok(sym.ID, yytext());}
					case -102:
						break;
					case 103:
						{return tok(sym.ID, yytext());}
					case -103:
						break;
					case 104:
						{return tok(sym.ID, yytext());}
					case -104:
						break;
					case 105:
						{return tok(sym.ID, yytext());}
					case -105:
						break;
					case 106:
						{return tok(sym.ID, yytext());}
					case -106:
						break;
					case 107:
						{return tok(sym.ID, yytext());}
					case -107:
						break;
					case 108:
						{return tok(sym.ID, yytext());}
					case -108:
						break;
					case 109:
						{return tok(sym.ID, yytext());}
					case -109:
						break;
					case 110:
						{return tok(sym.ID, yytext());}
					case -110:
						break;
					case 111:
						{return tok(sym.ID, yytext());}
					case -111:
						break;
					case 112:
						{return tok(sym.ID, yytext());}
					case -112:
						break;
					case 113:
						{return tok(sym.ID, yytext());}
					case -113:
						break;
					case 114:
						{return tok(sym.ID, yytext());}
					case -114:
						break;
					case 115:
						{return tok(sym.ID, yytext());}
					case -115:
						break;
					case 116:
						{return tok(sym.ID, yytext());}
					case -116:
						break;
					case 117:
						{return tok(sym.ID, yytext());}
					case -117:
						break;
					case 118:
						{return tok(sym.ID, yytext());}
					case -118:
						break;
					case 119:
						{return tok(sym.ID, yytext());}
					case -119:
						break;
					case 120:
						{return tok(sym.ID, yytext());}
					case -120:
						break;
					case 121:
						{return tok(sym.ID, yytext());}
					case -121:
						break;
					case 122:
						{return tok(sym.ID, yytext());}
					case -122:
						break;
					case 123:
						{return tok(sym.ID, yytext());}
					case -123:
						break;
					case 124:
						{return tok(sym.ID, yytext());}
					case -124:
						break;
					case 125:
						{return tok(sym.ID, yytext());}
					case -125:
						break;
					case 126:
						{return tok(sym.ID, yytext());}
					case -126:
						break;
					case 127:
						{return tok(sym.ID, yytext());}
					case -127:
						break;
					case 128:
						{return tok(sym.ID, yytext());}
					case -128:
						break;
					case 129:
						{return tok(sym.ID, yytext());}
					case -129:
						break;
					case 130:
						{return tok(sym.ID, yytext());}
					case -130:
						break;
					case 131:
						{return tok(sym.ID, yytext());}
					case -131:
						break;
					case 132:
						{return tok(sym.ID, yytext());}
					case -132:
						break;
					case 133:
						{return tok(sym.ID, yytext());}
					case -133:
						break;
					case 134:
						{return tok(sym.ID, yytext());}
					case -134:
						break;
					case 135:
						{return tok(sym.ID, yytext());}
					case -135:
						break;
					case 136:
						{return tok(sym.ID, yytext());}
					case -136:
						break;
					case 137:
						{return tok(sym.ID, yytext());}
					case -137:
						break;
					case 138:
						{return tok(sym.ID, yytext());}
					case -138:
						break;
					case 139:
						{return tok(sym.ID, yytext());}
					case -139:
						break;
					case 140:
						{return tok(sym.ID, yytext());}
					case -140:
						break;
					case 141:
						{return tok(sym.ID, yytext());}
					case -141:
						break;
					case 142:
						{return tok(sym.ID, yytext());}
					case -142:
						break;
					case 143:
						{return tok(sym.ID, yytext());}
					case -143:
						break;
					case 144:
						{return tok(sym.ID, yytext());}
					case -144:
						break;
					case 145:
						{return tok(sym.ID, yytext());}
					case -145:
						break;
					case 146:
						{return tok(sym.ID, yytext());}
					case -146:
						break;
					case 147:
						{return tok(sym.ID, yytext());}
					case -147:
						break;
					case 148:
						{return tok(sym.ID, yytext());}
					case -148:
						break;
					case 149:
						{return tok(sym.ID, yytext());}
					case -149:
						break;
					case 150:
						{return tok(sym.ID, yytext());}
					case -150:
						break;
					case 151:
						{return tok(sym.ID, yytext());}
					case -151:
						break;
					case 152:
						{return tok(sym.ID, yytext());}
					case -152:
						break;
					case 153:
						{return tok(sym.ID, yytext());}
					case -153:
						break;
					case 154:
						{return tok(sym.ID, yytext());}
					case -154:
						break;
					case 155:
						{return tok(sym.ID, yytext());}
					case -155:
						break;
					case 156:
						{return tok(sym.ID, yytext());}
					case -156:
						break;
					case 157:
						{return tok(sym.ID, yytext());}
					case -157:
						break;
					case 158:
						{return tok(sym.ID, yytext());}
					case -158:
						break;
					case 159:
						{return tok(sym.ID, yytext());}
					case -159:
						break;
					case 160:
						{return tok(sym.ID, yytext());}
					case -160:
						break;
					case 161:
						{return tok(sym.ID, yytext());}
					case -161:
						break;
					case 162:
						{return tok(sym.ID, yytext());}
					case -162:
						break;
					case 163:
						{return tok(sym.ID, yytext());}
					case -163:
						break;
					case 164:
						{return tok(sym.ID, yytext());}
					case -164:
						break;
					case 165:
						{return tok(sym.ID, yytext());}
					case -165:
						break;
					case 166:
						{return tok(sym.ID, yytext());}
					case -166:
						break;
					case 167:
						{return tok(sym.ID, yytext());}
					case -167:
						break;
					case 168:
						{return tok(sym.ID, yytext());}
					case -168:
						break;
					case 169:
						{return tok(sym.ID, yytext());}
					case -169:
						break;
					case 170:
						{return tok(sym.ID, yytext());}
					case -170:
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
