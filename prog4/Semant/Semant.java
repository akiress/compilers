package Semant;
import Translate.Exp;
import Types.Type;
import java.util.Hashtable;
import Translate.Level;
import Symbol.Symbol;

public class Semant {
  Env env;
  Level level;
  
  public Semant(Frame.Frame frame, ErrorMsg.ErrorMsg err) {
    this(new Env(err), new Level(frame));
  }
  
  Semant(Env e, Level l) {
    env = e;
    level = l;
  }

  private void error(int pos, String msg) {
    env.errorMsg.error(pos, msg);
  }

  static final Types.VOID   VOID   = new Types.VOID();
  static final Types.INT    INT    = new Types.INT();
  static final Types.STRING STRING = new Types.STRING();
  static final Types.NIL    NIL    = new Types.NIL();

  private Exp checkInt(ExpTy et, int pos) {
    if (!INT.coerceTo(et.ty))
      error(pos, "integer required");
    return et.exp;
  }

  private Exp checkComparable(ExpTy et, int pos) {
    Type a = et.ty.actual();
    if (!(a instanceof Types.INT
      || a instanceof Types.STRING
      || a instanceof Types.NIL
      || a instanceof Types.RECORD
      || a instanceof Types.ARRAY))
      error(pos, "integer, string, nil, record or array required");
    return et.exp;
  }

  private Exp checkOrderable(ExpTy et, int pos) {
    Type a = et.ty.actual();
    if (!(a instanceof Types.INT
      || a instanceof Types.STRING))
      error(pos, "integer or string required");
    return et.exp;
  }

  ExpTy transExp(Absyn.Exp e) {
    ExpTy result;

    if (e == null)
      return new ExpTy(null, VOID);
    else if (e instanceof Absyn.VarExp)
      result = transExp((Absyn.VarExp)e);
    else if (e instanceof Absyn.NilExp)
      result = transExp((Absyn.NilExp)e);
    else if (e instanceof Absyn.IntExp)
      result = transExp((Absyn.IntExp)e);
    else if (e instanceof Absyn.StringExp)
      result = transExp((Absyn.StringExp)e);
    else if (e instanceof Absyn.CallExp)
      result = transExp((Absyn.CallExp)e);
    else if (e instanceof Absyn.OpExp)
      result = transExp((Absyn.OpExp)e);
    else if (e instanceof Absyn.RecordExp)
      result = transExp((Absyn.RecordExp)e);
    else if (e instanceof Absyn.SeqExp)
      result = transExp((Absyn.SeqExp)e);
    else if (e instanceof Absyn.AssignExp)
      result = transExp((Absyn.AssignExp)e);
    else if (e instanceof Absyn.IfExp)
      result = transExp((Absyn.IfExp)e);
    else if (e instanceof Absyn.WhileExp)
      result = transExp((Absyn.WhileExp)e);
    else if (e instanceof Absyn.ForExp)
      result = transExp((Absyn.ForExp)e);
    else if (e instanceof Absyn.BreakExp)
      result = transExp((Absyn.BreakExp)e);
    else if (e instanceof Absyn.LetExp)
      result = transExp((Absyn.LetExp)e);
    else if (e instanceof Absyn.ArrayExp)
      result = transExp((Absyn.ArrayExp)e);
    else throw new Error("Semant.transExp");
    e.type = result.ty;
    return result;
  }

  ExpTy transExp(Absyn.VarExp e) {
    return transVar(e.var);
  }

  ExpTy transVar(Absyn.Var v) {
    return transVar(v, false);
  }

  ExpTy transVar(Absyn.Var v, boolean lhs) {
    if (v instanceof Absyn.SimpleVar)
      return transVar((Absyn.SimpleVar)v, lhs);
    if (v instanceof Absyn.FieldVar)
      return transVar((Absyn.FieldVar)v);
    if (v instanceof Absyn.SubscriptVar)
      return transVar((Absyn.SubscriptVar)v);
    throw new Error("Semant.transVar");
  }

  ExpTy transVar(Absyn.SimpleVar v, boolean lhs) {
    Entry x = (Entry)env.venv.get(v.name);
    if (x instanceof VarEntry) {
      VarEntry ent = (VarEntry)x;
      if (lhs && ent instanceof LoopVarEntry)
    error(v.pos, "assignment to loop index");
      return new ExpTy(null, ent.ty);
    }
    error(v.pos, "undeclared variable: " + v.name);
    return new ExpTy(null, VOID);
  }

  ExpTy transVar(Absyn.FieldVar v) {
    ExpTy var = transVar(v.var);
    Type actual = var.ty.actual();
    if (actual instanceof Types.RECORD) {
      for (Types.RECORD field = (Types.RECORD)actual;
       field != null;
       field = field.tail) {
    if (field.fieldName == v.field)
      return new ExpTy(null, field.fieldType);
      }
      error(v.pos, "undeclared field: " + v.field);
    } else
      error(v.var.pos, "record required");
    return new ExpTy(null, VOID);
  }

  ExpTy transVar(Absyn.SubscriptVar v) {
    ExpTy var = transVar(v.var);
    ExpTy index = transExp(v.index);
    checkInt(index, v.index.pos);
    Type actual = var.ty.actual();
    if (actual instanceof Types.ARRAY) {
      Types.ARRAY array = (Types.ARRAY)actual;
      return new ExpTy(null, array.element);
    }
    error(v.var.pos, "array required");
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.NilExp e) {
    return new ExpTy(null, NIL);
  }

  ExpTy transExp(Absyn.IntExp e) {
    return new ExpTy(null, INT);
  }

  ExpTy transExp(Absyn.StringExp e) {
    return new ExpTy(null, STRING);
  }

  ExpTy transExp(Absyn.CallExp e) {
    Entry x = (Entry)env.venv.get(e.func);
    if (x instanceof FunEntry) {
      FunEntry f = (FunEntry)x;
      transArgs(e.pos, f.formals, e.args);
      return new ExpTy(null, f.result);
    }
    error(e.pos, "undeclared function: " + e.func);
    return new ExpTy(null, VOID);
  }

  private void transArgs(int epos, Types.RECORD formal, Absyn.ExpList args) {
    if (formal == null) {
      if (args != null)
    error(args.head.pos, "too many arguments");
      return;
    }
    if (args == null) {
      error(epos, "missing argument for " + formal.fieldName);
      return;
    }
    ExpTy e = transExp(args.head);
    if (!e.ty.coerceTo(formal.fieldType))
      error(args.head.pos, "argument type mismatch");
    transArgs(epos, formal.tail, args.tail);
  }

  ExpTy transExp(Absyn.OpExp e) {
    ExpTy left = transExp(e.left);
    ExpTy right = transExp(e.right);

    switch (e.oper) {
    case Absyn.OpExp.PLUS:
    case Absyn.OpExp.MINUS:
    case Absyn.OpExp.MUL:
    case Absyn.OpExp.DIV:
      checkInt(left, e.left.pos);
      checkInt(right, e.right.pos);
      return new ExpTy(null, INT);
    case Absyn.OpExp.EQ:
    case Absyn.OpExp.NE:
      checkComparable(left, e.left.pos);
      checkComparable(right, e.right.pos);
      if (!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty)) 
    error(e.pos, "incompatible operands to equality operator");
      return new ExpTy(null, INT);
    case Absyn.OpExp.LT:
    case Absyn.OpExp.LE:
    case Absyn.OpExp.GT:
    case Absyn.OpExp.GE:
      checkOrderable(left, e.left.pos);
      checkOrderable(right, e.right.pos);
      if (!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty))
    error(e.pos, "incompatible operands to inequality operator");
      return new ExpTy(null, INT);
    default:
      throw new Error("unknown operator");
    }
  }

  ExpTy transExp(Absyn.RecordExp e) {
    Types.NAME name = (Types.NAME)env.tenv.get(e.typ);
    if (name != null) {
      Type actual = name.actual();
      if (actual instanceof Types.RECORD) {
    Types.RECORD r = (Types.RECORD)actual;
        transFields(e.pos, r, e.fields);
        return new ExpTy(null, name);
      }
      error(e.pos, "record type required");
    } else
      error(e.pos, "undeclared type: " + e.typ);
    return new ExpTy(null, VOID);
  }

  private void transFields(int epos, Types.RECORD f, Absyn.FieldExpList exp) {
    if (f == null) {
      if (exp != null)
        error(exp.pos, "too many expressions");
      return;
    }
    if (exp == null) {
      error(epos, "missing expression for " + f.fieldName);
      return;
    }
    ExpTy e = transExp(exp.init);
    if (exp.name != f.fieldName)
      error(exp.pos, "field name mismatch");
    if (!e.ty.coerceTo(f.fieldType))
      error(exp.pos, "field type mismatch");
    transFields(epos, f.tail, exp.tail);
  }

  ExpTy transExp(Absyn.SeqExp e) {
    Type type = VOID;
    for (Absyn.ExpList exp = e.list; exp != null; exp = exp.tail) {
      ExpTy et = transExp(exp.head);
      type = et.ty;
    }
    return new ExpTy(null, type);
  }

  ExpTy transExp(Absyn.AssignExp e) {
    ExpTy var = transVar(e.var, true);
    ExpTy exp = transExp(e.exp);
    if (!exp.ty.coerceTo(var.ty))
      error(e.pos, "assignment type mismatch");
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.IfExp e) {
    ExpTy test = transExp(e.test);
    checkInt(test, e.test.pos);
    ExpTy thenclause = transExp(e.thenclause);
    ExpTy elseclause = transExp(e.elseclause);
    if (!thenclause.ty.coerceTo(elseclause.ty)
    && !elseclause.ty.coerceTo(thenclause.ty))
      error(e.pos, "result type mismatch");
    return new ExpTy(null, elseclause.ty);
  }

  ExpTy transExp(Absyn.WhileExp e) {
    ExpTy test = transExp(e.test);
    checkInt(test, e.test.pos);
    Semant loop = new LoopSemant(env, level);
    ExpTy body = loop.transExp(e.body);
    if (!body.ty.coerceTo(VOID))
      error(e.body.pos, "result type mismatch");
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.ForExp e) {
    ExpTy lo = transExp(e.var.init);
    checkInt(lo, e.var.pos);
    ExpTy hi = transExp(e.hi);
    checkInt(hi, e.hi.pos);
    env.venv.beginScope();
    Translate.Access access = level.allocLocal(e.var.escape);
    e.var.entry = new LoopVarEntry(access, INT);
    env.venv.put(e.var.name, e.var.entry);
    Semant loop = new LoopSemant(env, level);
    ExpTy body = loop.transExp(e.body);
    env.venv.endScope();
    if (!body.ty.coerceTo(VOID))
      error(e.body.pos, "result type mismatch");
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.BreakExp e) {
    error(e.pos, "break outside loop");
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.LetExp e) {
    env.venv.beginScope();
    env.tenv.beginScope();
    for (Absyn.DecList d = e.decs; d != null; d = d.tail) {
      transDec(d.head);
    }
    ExpTy body = transExp(e.body);
    env.venv.endScope();
    env.tenv.endScope();
    return new ExpTy(null, body.ty);
  }

  ExpTy transExp(Absyn.ArrayExp e) {
    Types.NAME name = (Types.NAME)env.tenv.get(e.typ);
    ExpTy size = transExp(e.size);
    ExpTy init = transExp(e.init);
    checkInt(size, e.size.pos);
    if (name != null) {
      Type actual = name.actual();
      if (actual instanceof Types.ARRAY) {
    Types.ARRAY array = (Types.ARRAY)actual;
    if (!init.ty.coerceTo(array.element))
      error(e.init.pos, "element type mismatch");
    return new ExpTy(null, name);
      } else
    error(e.pos, "array type required");
    } else
      error(e.pos, "undeclared type: " + e.typ);
    return new ExpTy(null, VOID);
  }

  Exp transDec(Absyn.Dec d) {
    if (d instanceof Absyn.VarDec)
      return transDec((Absyn.VarDec)d);
    if (d instanceof Absyn.TypeDec)
      return transDec((Absyn.TypeDec)d);
    if (d instanceof Absyn.FunctionDec)
      return transDec((Absyn.FunctionDec)d);
    throw new Error("Semant.transDec");
  }

  Exp transDec(Absyn.VarDec d) {
    ExpTy init = transExp(d.init);
    Type type;
    if (d.typ == null) {
      if (init.ty.coerceTo(NIL))
    error(d.pos, "record type required");
      type = init.ty;
    } else {
      type = transTy(d.typ);
      if (!init.ty.coerceTo(type))
    error(d.pos, "assignment type mismatch");
    }
    Translate.Access access = level.allocLocal(d.escape);
    d.entry = new VarEntry(access, type);
    env.venv.put(d.name, d.entry);
    return null;
  }

  Exp transDec(Absyn.TypeDec d) {
    // 1st pass - handles the type headers
    // Using a local hashtable, check if there are two types 
    // with the same name in the same (consecutive) batch 
    // of mutually recursive types. See test38.tig!
    Hashtable hash = new Hashtable();
    for (Absyn.TypeDec type = d; type != null; type = type.next) {
      if (hash.put(type.name, type.name) != null)
        error(type.pos, "type redeclared");
      type.entry = new Types.NAME(type.name);
      env.tenv.put(type.name, type.entry);
    }

    // 2nd pass - handles the type bodies
    for (Absyn.TypeDec type = d; type != null; type = type.next) {
      Types.NAME name = (Types.NAME)type.entry;
      name.bind(transTy(type.ty));
    }

    // check for illegal cycle in type declarations
    for (Absyn.TypeDec type = d; type != null; type = type.next) {
      Types.NAME name = (Types.NAME)type.entry;
      if (name.isLoop())
        error(type.pos, "illegal type cycle");
    }
    return null;
  }

  Exp transDec(Absyn.FunctionDec d) {
    // 1st pass - handles the function headers
    Hashtable hash = new Hashtable();
    for (Absyn.FunctionDec f = d; f != null; f = f.next) {
      if (hash.put(f.name, f.name) != null)
        error(f.pos, "function redeclared");
      Types.RECORD fields = transTypeFields(new Hashtable(), f.params);
      Type type = transTy(f.result);
      Level nextLevel = new Level(level, f.name, escaped(f.params), f.leaf);
      f.entry = new FunEntry(nextLevel, fields, type);
      env.venv.put(f.name, f.entry);
    }
    // 2nd pass - handles the function bodies
    for (Absyn.FunctionDec f = d; f != null; f = f.next) {
      env.venv.beginScope();
      putTypeFields(f.entry.formals, f.entry.level.formals);
      Semant fun = new Semant(env, f.entry.level);
      ExpTy body = fun.transExp(f.body);
      if (!body.ty.coerceTo(f.entry.result))
    error(f.body.pos, "result type mismatch");
      env.venv.endScope();
    }
    return null;
  }

  private Types.RECORD transTypeFields(Hashtable hash, Absyn.FieldList f) {
    if (f == null)
      return null;
    Types.NAME name = (Types.NAME)env.tenv.get(f.typ);
    if (name == null)
      error(f.pos, "undeclared type: " + f.typ);
    if (hash.put(f.name, f.name) != null)
      error(f.pos, "function parameter/record field redeclared: " + f.name);
    return new Types.RECORD(f.name, name, transTypeFields(hash, f.tail));
  }

  private void putTypeFields (Types.RECORD f, Translate.AccessList a) {
    if (f == null)
      return;
    else {
      env.venv.put(f.fieldName, new VarEntry(a.head, f.fieldType));
      putTypeFields(f.tail, a.tail);
      return;
    }
  }

  Type transTy(Absyn.Ty t) {
    if (t instanceof Absyn.NameTy)
      return transTy((Absyn.NameTy)t);
    if (t instanceof Absyn.RecordTy)
      return transTy((Absyn.RecordTy)t);
    if (t instanceof Absyn.ArrayTy)
      return transTy((Absyn.ArrayTy)t);
    throw new Error("Semant.transTy");
  }

  Type transTy(Absyn.NameTy t) {
    if (t == null)
      return VOID;
    Types.NAME name = (Types.NAME)env.tenv.get(t.name);
    if (name != null)
      return name;
    error(t.pos, "undeclared type: " + t.name);
    return VOID;
  }

  Type transTy(Absyn.RecordTy t) {
    Types.RECORD type = transTypeFields(new Hashtable(), t.fields);
    if (type != null)
      return type;
    return VOID;
  }

  Type transTy(Absyn.ArrayTy t) {
    Types.NAME name = (Types.NAME)env.tenv.get(t.typ);
    if (name != null)
      return new Types.ARRAY(name);
    error(t.pos, "undeclared type: " + t.typ);
    return VOID;
  }
  
/* transProg & other new things */
  private Util.BoolList escaped(Absyn.FieldList f) {
    if (f == null)
      return null;
    else
      return new Util.BoolList(f.escape, escaped(f.tail));
  }
  
  public void transProg(Absyn.Exp exp) {
    new FindEscape.FindEscape(exp);
    level = new Level(level, Symbol.symbol("tigermain"), null);
    transExp(exp);
  }
}

class LoopSemant extends Semant {
  LoopSemant(Env e, Level l) {
    super(e, l);
  }

  ExpTy transExp(Absyn.BreakExp e) {
    return new ExpTy(null, VOID);
  }
}
