package Translate;

public class Access {
  Level home;
  Frame.Access acc;
  Access(Level h, Frame.Access a) {
    home = h;
    acc = a;
  }
  public String toString() {
    return "[" + home.frame.name.toString() + "," + acc.toString() + "]";
  }
}
