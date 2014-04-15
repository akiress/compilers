package Translate;

public class ProcFrag extends Frag {
  public Tree.Stm body;
  public Frame.Frame frame;
  public ProcFrag(Tree.Stm b, Frame.Frame f) {
    body = b;
    frame = f;
  }
}
