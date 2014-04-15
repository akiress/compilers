package Translate;
import Symbol.Symbol;
import Tree.BINOP;
import Tree.CJUMP;
import Temp.Temp;
import Temp.Label;

public class Translate {
  public Frame.Frame frame;
  public Translate(Frame.Frame f) {
    frame = f;
  }
  private Frag frags;
  public void procEntryExit(Level level, Exp body) {
    Frame.Frame myframe = level.frame;
    Tree.Exp bodyExp = body.unEx();
    Tree.Stm bodyStm;
    if (bodyExp != null)
      bodyStm = MOVE(TEMP(myframe.RV()), bodyExp);
    else
      bodyStm = body.unNx();
    ProcFrag frag = new ProcFrag(myframe.procEntryExit1(bodyStm), myframe);
    frag.next = frags;
    frags = frag;
  }
  public Frag getResult() {
    return frags;
  }

  private static Tree.Exp CONST(int value) {
    return new Tree.CONST(value);
  }
  private static Tree.Exp NAME(Label label) {
    return new Tree.NAME(label);
  }
  private static Tree.Exp TEMP(Temp temp) {
    return new Tree.TEMP(temp);
  }
  private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) {
    return new Tree.BINOP(binop, left, right);
  }
  private static Tree.Exp MEM(Tree.Exp exp) {
    return new Tree.MEM(exp);
  }
  private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args) {
    return new Tree.CALL(func, args);
  }
  private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
    if (stm == null)
      return exp;
    return new Tree.ESEQ(stm, exp);
  }

  private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) {
    return new Tree.MOVE(dst, src);
  }
  private static Tree.Stm EXP(Tree.Exp exp) {
    return new Tree.EXP(exp);
  }
  private static Tree.Stm JUMP(Label target) {
    return new Tree.JUMP(target);
  }
  private static
  Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) {
    return new Tree.CJUMP(relop, l, r, t, f);
  }
  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
    if (left == null)
      return right;
    if (right == null)
      return left;
    return new Tree.SEQ(left, right);
  }
  private static Tree.Stm LABEL(Label label) {
    return new Tree.LABEL(label);
  }

  private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail) {
    return new Tree.ExpList(head, tail);
  }
  private static Tree.ExpList ExpList(Tree.Exp head) {
    return ExpList(head, null);
  }
  private static Tree.ExpList ExpList(ExpList exp) {
    if (exp == null)
      return null;
    return ExpList(exp.head.unEx(), ExpList(exp.tail));
  }

  public Exp Error() {
    return new Ex(CONST(0));
  }

  public Exp SimpleVar(Access access, Level level) {
    Level l = level;
    Tree.Exp fp = new Tree.TEMP(level.frame.FP());
    while(l != access.home) {
      fp = l.formals.head.acc.exp(fp);
      l = l.parent;
    }
    return new Ex(access.acc.exp(fp));
  }

  public Exp FieldVar(Exp record, int index) {
    return new Ex(BINOP(0, MEM(record.unEx()), CONST(index)));
  }

  public Exp SubscriptVar(Exp array, Exp index) {
    return new Ex(BINOP(0, MEM(array.unEx()), index.unEx()));
  }

  public Exp NilExp() {
    return new Nx(null);
  }

  public Exp IntExp(int value) {
    return new Ex(CONST(value));
  }

  private java.util.Hashtable strings = new java.util.Hashtable();
  public Exp StringExp(String lit) {
    String u = lit.intern();
    Label lab = (Label)strings.get(u);
    if (lab == null) {
      lab = new Label();
      strings.put(u, lab);
      DataFrag frag = new DataFrag(frame.string(lab, u));
      frag.next = frags;
      frags = frag;
    }
    return new Ex(NAME(lab));
  }

  private Tree.Exp CallExp(Symbol f, ExpList args, Level from) {
    return frame.externalCall(f.toString(), ExpList(args));
  }

  private Tree.Exp CallExp(Level f, ExpList args, Level from) {
    return frame.externalCall(f.name().toString(), ExpList(args));
  }

  public Exp FunExp(Symbol f, ExpList args, Level from) {
    return new Ex(CallExp(f, args, from));
  }

  public Exp FunExp(Level f, ExpList args, Level from) {
    return new Ex(CallExp(f, args, from));
  }

  public Exp ProcExp(Symbol f, ExpList args, Level from) {
    return new Nx(EXP(CallExp(f, args, from)));
  }

  public Exp ProcExp(Level f, ExpList args, Level from) {
    return new Nx(EXP(CallExp(f, args, from)));
  }

  public Exp OpExp(int op, Exp left, Exp right) {
    switch(op)
    {
      case Absyn.OpExp.PLUS:
      return new Ex(BINOP(BINOP.PLUS, left.unEx(), right.unEx()));
      case Absyn.OpExp.MINUS:
      return new Ex(BINOP(BINOP.MINUS, left.unEx(), right.unEx()));
      case Absyn.OpExp.DIV:
      return new Ex(BINOP(BINOP.DIV, left.unEx(), right.unEx()));
      case Absyn.OpExp.MUL:
      return new Ex(BINOP(BINOP.MUL, left.unEx(), right.unEx()));
      case Absyn.OpExp.EQ:
      return new RelCx(CJUMP.EQ, left.unEx(), right.unEx());
      case Absyn.OpExp.GE:
      return new RelCx(CJUMP.GE, left.unEx(), right.unEx());
      case Absyn.OpExp.GT:
      return new RelCx(CJUMP.GT, left.unEx(), right.unEx());
      case Absyn.OpExp.LE:
      return new RelCx(CJUMP.LE, left.unEx(), right.unEx());
      case Absyn.OpExp.LT:
      return new RelCx(CJUMP.LT, left.unEx(), right.unEx());
      case Absyn.OpExp.NE:
      return new RelCx(CJUMP.NE, left.unEx(), right.unEx());
    }
    return Error();
  }
  
  public Exp StrOpExp(int op, Exp left, Exp right) {
    switch(op)
    {
      case Absyn.OpExp.EQ:
      return new RelCx(CJUMP.EQ, left.unEx(), right.unEx());
      case Absyn.OpExp.GE:
      return new RelCx(CJUMP.GE, left.unEx(), right.unEx());
      case Absyn.OpExp.GT:
      return new RelCx(CJUMP.GT, left.unEx(), right.unEx());
      case Absyn.OpExp.LE:
      return new RelCx(CJUMP.LE, left.unEx(), right.unEx());
      case Absyn.OpExp.LT:
      return new RelCx(CJUMP.LT, left.unEx(), right.unEx());
      case Absyn.OpExp.NE:
      return new RelCx(CJUMP.NE, left.unEx(), right.unEx());
    }
    return Error();
  }

  public Exp RecordExp(ExpList init) {
    return new Nx(null);
  }

  public Exp SeqExp(ExpList e) {
    Nx exp = null;
    if (e != null)
      exp = new Nx(null);
    while (e != null) {
      if (e.head != null)
        exp = new Nx(SEQ(e.head.unNx(), exp.unNx()));
      e = e.tail;
    }
    return exp;
  }

  public Exp AssignExp(Exp lhs, Exp rhs) {
    return new Nx(MOVE(lhs.unEx(), rhs.unEx()));
  }

  public Exp IfExp(Exp cc, Exp aa, Exp bb) {
    return new IfThenElseExp(cc, aa, bb);
  }

  public Exp WhileExp(Exp cond, Exp body, Label done) {
    Label test = new Label();
    Label top = new Label();
    return new Nx(new Tree.SEQ(new Tree.JUMP(test),
      new Tree.SEQ(new Tree.LABEL(top),
        new Tree.SEQ(body.unNx(),
          new Tree.SEQ(new Tree.LABEL(test),
            new Tree.SEQ(cond.unCx(top, done),
              new Tree.LABEL(done)))))));
  }

  public Exp ForExp(Access i, Exp lo, Exp hi, Exp body, Label done) {
    Access limit = i;
    Level d = i.home;
    return (new Nx(SEQ(AssignExp(SimpleVar(i, d), lo).unNx(), AssignExp(
        SimpleVar(limit, d), hi).unNx())));
  }

  public Exp BreakExp(Label done) {
    return new Nx(JUMP(done));
  }

  public Exp LetExp(ExpList lets, Exp body) {
    ExpList tmp = null;
    ExpList iter = lets;
    Exp exp = body;
    
    if (iter != null) {
      return new Ex(ESEQ(iter.head.unNx(), body.unEx()));
    } else {
      return new Nx(SEQ(lets.head.unNx(), body.unNx()));
    }
  }

  public Exp ArrayExp(Exp size, Exp init) {
    Label initArray = new Label("initArray");
    return new Ex(new Tree.CALL(NAME(initArray), new Tree.ExpList(size
        .unEx(), new Tree.ExpList(init.unEx(), null))));
  }

  public Exp VarDec(Access a, Exp init) {
    return new Nx(MOVE(a.acc.exp(TEMP(a.home.frame.FP())), init.unEx()));
  }

  public Exp TypeDec() {
    return new Nx(null);
  }

  public Exp FunctionDec() {
    return new Nx(null);
  }
}
