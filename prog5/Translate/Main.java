package Translate;
import Parse.Parse;
import Semant.Semant;

public class Main {
  static Frame.Frame frame = new Mips.MipsFrame();

  public static void main(String argv[])  {
    for (int i = 0; i < argv.length; ++i) {
      String filename = argv[i];
      if (argv.length > 1)
	System.out.println("***Processing: " + filename);
      Parse parse = new Parse(filename);
      Translate translate = new Translate(frame);
      Semant semant = new Semant(translate, parse.errorMsg);
      Frag frags = semant.transProg(parse.absyn);
      if (!parse.errorMsg.anyErrors) {
	java.io.PrintWriter writer = new java.io.PrintWriter(System.out);
	Tree.Print printer = new Tree.Print(writer);
	for (Frag f = frags; f != null; f = f.next)
	  if (f instanceof DataFrag) {
	    DataFrag d = (DataFrag)f;
	    writer.println(d.data);
	  } else {
	    ProcFrag p = (ProcFrag)f;
	    writer.println(p.frame.name + ":");
	    if (p.body != null)
	      printer.prStm(p.body);
	  }
	writer.flush();
      }
    }
  }
}
