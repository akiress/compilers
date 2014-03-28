package Temp;

public class TempList {
  public Temp head;
  public TempList tail;
  public TempList(Temp h, TempList t) {head=h; tail=t;}
  public TempList(TempList a, TempList b) {
    if (a.tail == null) {
      head = a.head;
      tail = b;
    } else {
      head = a.head;
      tail = new TempList(a.tail, b);
    }
  }
}
