package Semant;

class LoopVarEntry extends VarEntry {
  LoopVarEntry(Types.Type t) {
    super(t);
  }

  LoopVarEntry(Translate.Access access, Types.Type t) {
    super(t);
  }
}
