package Mips;

class InFrame extends Frame.Access {
  int offset;
  InFrame(int o) {
    offset = o;
  }

  public Tree.Exp exp(Tree.Exp fp) {
    return new Tree.MEM
      (new Tree.BINOP(Tree.BINOP.PLUS, fp, new Tree.CONST(offset)));
  }

  public String toString() {
    Integer offset = new Integer(this.offset);
    return offset.toString();
  }
}
