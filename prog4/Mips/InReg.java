package Mips;
import Temp.Temp;

class InReg extends Frame.Access {
  Temp temp;
  InReg(Temp t) {
    temp = t;
  }

  public String toString() {
    return temp.toString();
  }
}
