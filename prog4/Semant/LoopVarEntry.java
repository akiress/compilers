package Semant;

class LoopVarEntry extends VarEntry {
    LoopVarEntry(Types.Type t) {
        super(t);
    }

    LoopVarEntry(Translate.Access a, Types.Type t) {
        super(a, new Types.INT());
    }
}
