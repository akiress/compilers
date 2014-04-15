package Translate;
import Temp.Label;

class RelCx extends Cx {
  int op;
  Tree.Exp left, right;

  RelCx(int o, Tree.Exp l, Tree.Exp r) {
    op = o;
    left = l;
    right = r;
  }

  Tree.Stm unCx(Label t, Label f) {
    return new Tree.CJUMP(op, left, right, t, f);
  }
}
