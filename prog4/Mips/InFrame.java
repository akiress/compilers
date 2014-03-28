package Mips;

class InFrame extends Frame.Access {
  int offset;
  InFrame(int o) {
    offset = o;
  }

  public String toString() {
    Integer offset = new Integer(this.offset);
    return offset.toString();
  }
}
