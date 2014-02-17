package Parse;
import java.io.PrintWriter;

public class Main {

  public static void main(String argv[]) throws java.io.IOException {
    for (int i = 0; i < argv.length; ++i) {
      String filename = argv[i];
      if (argv.length > 1)
	System.out.println("***Processing: " + filename);
      Parse parse = new Parse(filename);
      PrintWriter writer = new PrintWriter(System.out);
      Absyn.Print printer = new Absyn.Print(writer);
      printer.prExp(parse.absyn, 0);
      writer.println();
      writer.flush();
    }
  }

}
