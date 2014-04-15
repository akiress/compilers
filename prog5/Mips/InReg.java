package Mips;
import Temp.Temp;

class InReg extends Frame.Access {
  Temp temp;
  InReg(Temp t) {
    temp = t;
  }

  public Tree.Exp exp(Tree.Exp fp) {
    return new Tree.TEMP(temp);
  }

  public String toString() {
    return temp.toString();
  }
}
