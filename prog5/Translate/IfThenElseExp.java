package Translate;
import Temp.Temp;
import Temp.Label;

class IfThenElseExp extends Exp {
  Exp cond, a, b;
  Label t = new Label();
  Label f = new Label();
  Label join = new Label();

  IfThenElseExp(Exp cc, Exp aa, Exp bb) {
    cond = cc;
    a = aa;
    b = bb;
  }

  Tree.Stm unCx(Label tt, Label ff) {
        // This is the naive implementation; you should extend it to eliminate
        // unnecessary JUMP nodes
    Tree.Stm aStm = a.unCx(tt, ff);
    Tree.Stm bStm = b.unCx(tt, ff);

    Tree.Stm condStm = cond.unCx(t, f);

    if (aStm == null && bStm == null)
      return condStm;
    if (aStm == null)
      return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(f), bStm));
    if (bStm == null)
      return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(t), aStm));
    return new Tree.SEQ(condStm,
      new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm),
       new Tree.SEQ(new Tree.LABEL(f), bStm)));
  }

  Tree.Exp unEx() {
    Temp r = new Temp();
    return new Tree.ESEQ(
     new Tree.SEQ(cond.unCx(t, f),
       new Tree.SEQ(new Tree.LABEL(t),
         new Tree.SEQ(new Tree.MOVE(new Tree.TEMP(r), a.unEx()),
           new Tree.SEQ(new Tree.JUMP(join),
             new Tree.SEQ(new Tree.LABEL(f),
              new Tree.SEQ(new Tree.MOVE(new Tree.TEMP(r), b.unEx()),
                 new Tree.LABEL(join))))))),
     new Tree.TEMP(r));
  }

  Tree.Stm unNx() {
    return new Tree.SEQ(cond.unCx(t,f),
      new Tree.SEQ(new Tree.LABEL(t),
        new Tree.SEQ(a.unNx(),
          new Tree.SEQ(new Tree.JUMP(join),
            new Tree.SEQ(new Tree.LABEL(f),
              new Tree.SEQ(b.unNx(),
                new Tree.LABEL(join)))))));
  }
}
