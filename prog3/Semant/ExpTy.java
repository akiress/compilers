package Semant;

import Translate.Exp;
import Types.Type;

class ExpTy {
  Exp exp;
  Type ty;
  ExpTy(Exp e, Type t) {
    exp = e;
    ty = t;
    //System.out.println(exp);
    //System.out.println(ty);
  }
}
