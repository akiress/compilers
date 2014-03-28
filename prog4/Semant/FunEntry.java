package Semant;

public class FunEntry extends Entry {
  Translate.Level level;
  public Types.RECORD formals;
  public Types.Type result;
  FunEntry(Types.RECORD f, Types.Type r) {
    this(null, f, r);
  }
  FunEntry(Translate.Level v, Types.RECORD f, Types.Type r) {
    level = v;
    formals = f;
    result = r;
  }
}
