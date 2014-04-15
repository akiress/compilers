package Mips;
import java.util.Hashtable;
import Symbol.Symbol;
import Temp.Temp;
import Temp.Label;
import Frame.Frame;
import Frame.Access;
import Frame.AccessList;

public class MipsFrame extends Frame {

  private int count = 0;
  public Frame newFrame(Symbol name, Util.BoolList formals) {
    Label label;
    if (name == null)
      label = new Label();
    else if (this.name != null)
      label = new Label(this.name + "." + name + "." + count++);
    else
      label = new Label(name);
    return new MipsFrame(label, formals);
  }

  public MipsFrame() {}
  private MipsFrame(Label n, Util.BoolList f) {
    name = n;
    formals = allocFormals(0, f);
  }

  private static final int wordSize = 4;
  public int wordSize() { return wordSize; }

  private int offset = 0;
  public Access allocLocal(boolean escape) {
    if (escape) {
      offset -= wordSize;
      return new InFrame(offset);
    } else
      return new InReg(new Temp());
  }

  private AccessList allocFormals(int offset, Util.BoolList formals) {
    if (formals == null)
      return null;
    Access a;
    if (formals.head)
      a = new InFrame(offset);
    else
      a = new InReg(new Temp());
    return new AccessList(a, allocFormals(offset + wordSize, formals.tail));
  }

  static final Temp V0 = new Temp(); // function result
  static final Temp FP = new Temp(); // virtual frame pointer (eliminated)

  public Temp FP() { return FP; }
  public Temp RV() { return V0; }

  private static Hashtable labels = new Hashtable();
  public Tree.Exp externalCall(String func, Tree.ExpList args) {
    String u = func.intern();
    Label l = (Label)labels.get(u);
    if (l == null) {
      l = new Label("_" + u);
      labels.put(u, l);
    }
    return new Tree.CALL(new Tree.NAME(l), args);
  }

  public Tree.Stm procEntryExit1(Tree.Stm body) {
    return body;
  }

  public String string(Label lab, String string) {
    int length = string.length();
    String lit = "";
    for (int i = 0; i < length; i++) {
      char c = string.charAt(i);
      switch(c) {
      case '\b': lit += "\\b"; break;
      case '\t': lit += "\\t"; break;
      case '\n': lit += "\\n"; break;
      case '\f': lit += "\\f"; break;
      case '\r': lit += "\\r"; break;
      case '\"': lit += "\\\""; break;
      case '\\': lit += "\\\\"; break;
      default:
        if (c < ' ' || c > '~') {
          int v = (int)c;
          lit += "\\" + ((v>>6)&7) + ((v>>3)&7) + (v&7);
        } else
          lit += c;
        break;
      }
    }
    return "\t.data\n\t.word " + length + "\n" + lab.toString()
      + ":\t.asciiz\t\"" + lit + "\"";
  }

  private static final Label badPtr = new Label("_BADPTR");
  public Label badPtr() {
    return badPtr;
  }

  private static final Label badSub = new Label("_BADSUB");
  public Label badSub() {
    return badSub;
  }

}
