package Frame;

public abstract class Frame {
  public Temp.Label name;
  public AccessList formals;
  abstract public Frame newFrame(Symbol.Symbol name, Util.BoolList formals);
  abstract public Access allocLocal(boolean escape);
}
