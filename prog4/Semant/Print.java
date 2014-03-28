package Semant;

public class Print {
  java.io.PrintWriter out;
  private Types.Print types;

  public Print(java.io.PrintWriter o) {
    out = o;
    types = new Types.Print(o);
  }

  void indent(int d) {
    for(int i=0; i<d; i++) 
      out.print(' ');
  }

  void say(String s) {
    out.print(s);
  }

  void say(int i) {
    out.print(i);
  }

  void say(boolean b) {
    out.print(b);
  }

  void sayln(String s) {
    out.println(s);
  }

  public void prEntry(Entry entry, int d) {
    if (entry instanceof FunEntry)
      prEntry((FunEntry)entry, d);
    else if (entry instanceof VarEntry)
      prEntry((VarEntry)entry, d);
    else throw new Error("Semant.prEntry");
  }

  void prEntry(FunEntry entry, int d) {
    say(":"); types.prType(entry.formals, d+1); sayln("");
    indent(d+1); say("->"); types.prType(entry.result, d+3);
    if (entry.level != null) {
      sayln(""); indent(d); say("@" + entry.level.name().toString());
      for (Translate.AccessList f = entry.level.frameFormals;
	   f != null;
	   f = f.tail) {
	sayln(""); indent(d+1); say(f.head.toString());
      }
    }
  }

  void prEntry(VarEntry entry, int d) {
    say(":"); types.prType(entry.ty, d+1);
    if (entry.access != null) {
      sayln(""); indent(d); say("@" + entry.access.toString());
    }
  }
}
