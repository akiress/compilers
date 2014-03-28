package Semant;

public class VarEntry extends Entry {
    Translate.Access access;
    public Types.Type ty;
    VarEntry(Types.Type t) {
        ty = t;
    }

    VarEntry(Translate.Access acc, Types.Type t) {
        access = acc;
        ty = t;
    }
}
