package Semant;
import Translate.Exp;
import Types.Type;
import java.util.HashSet;

public class Semant {
  Env env;
  int LOOPNUM = 0;

  public Semant(ErrorMsg.ErrorMsg err) {
    this(new Env(err));
  }

  Semant(Env e) {
    env = e;
  }

  public void transProg(Absyn.Exp exp) {
    transExp(exp);
  }

  private void Error(int pos, String msg) {
    env.errorMsg.error(pos, msg);
  }

  static final Types.VOID     VOID    = new Types.VOID();
  static final Types.INT      INT     = new Types.INT();
  static final Types.STRING   STRING  = new Types.STRING();
  static final Types.NIL      NIL     = new Types.NIL();

  private Exp checkInt(ExpTy et, int pos) {
    if (!INT.coerceTo(et.ty))
      Error(pos, "integer required");
    return et.exp;
  }

  /***
  UML::Dec
  ***/

  ExpTy transExp(Absyn.Exp e) {
    ExpTy result;

    if (e == null) {
      return new ExpTy(null, VOID);
    } else if (e instanceof Absyn.VarExp) {
      result = transExp((Absyn.VarExp)e);
    } else if (e instanceof Absyn.NilExp) {
      result = transExp((Absyn.NilExp)e);
    } else if (e instanceof Absyn.IntExp) {
      result = transExp((Absyn.IntExp)e);
    } else if (e instanceof Absyn.StringExp) {
      result = transExp((Absyn.StringExp)e);
    } else if (e instanceof Absyn.CallExp) {
      result = transExp((Absyn.CallExp)e);
    } else if (e instanceof Absyn.OpExp) {
      result = transExp((Absyn.OpExp)e);
    } else if (e instanceof Absyn.RecordExp) {
      result = transExp((Absyn.RecordExp)e);
    } else if (e instanceof Absyn.SeqExp) {
      result = transExp((Absyn.SeqExp)e);
    } else if (e instanceof Absyn.AssignExp) {
      result = transExp((Absyn.AssignExp)e);
    } else if (e instanceof Absyn.IfExp) {
      result = transExp((Absyn.IfExp)e);
    } else if (e instanceof Absyn.WhileExp) {
      result = transExp((Absyn.WhileExp)e);
    } else if (e instanceof Absyn.ForExp) {
      result = transExp((Absyn.ForExp)e);
    } else if (e instanceof Absyn.BreakExp) {
      result = transExp((Absyn.BreakExp)e);
    } else if (e instanceof Absyn.LetExp) {
      result = transExp((Absyn.LetExp)e);
    } else if (e instanceof Absyn.ArrayExp) {
      result = transExp((Absyn.ArrayExp)e);
    } else {
      throw new Error("Semant.transExp");
    }

    e.type = result.ty;
    System.out.println("transExp(Absyn.Exp)");
    return result;
  }

  ExpTy transVar(Absyn.Var e) {
    if (e instanceof Absyn.SimpleVar) {
      return transVar((Absyn.SimpleVar)e);
    } else if (e instanceof Absyn.FieldVar) {
      return transVar((Absyn.FieldVar)e);
    } else if (e instanceof Absyn.SubscriptVar) {
      return transVar((Absyn.SubscriptVar)e);
    } else {
      throw new Error("transVar");
    }
  }

  Exp transDec(Absyn.Dec e) {
    if (e instanceof Absyn.FunctionDec) {
      return transDec((Absyn.FunctionDec)e);
    } else if (e instanceof Absyn.VarDec) {
      return transDec((Absyn.VarDec)e);
    } else if (e instanceof Absyn.TypeDec) {
      return transDec((Absyn.TypeDec)e);
    } else {
      throw new Error("transDec");
    }
  }

  Type transTy(Absyn.Ty e) {
    if (e instanceof Absyn.NameTy) {
      return transTy((Absyn.NameTy)e);
    } else if (e instanceof Absyn.RecordTy) {
      return transTy((Absyn.RecordTy)e);
    } else if (e instanceof Absyn.ArrayTy) {
      return transTy((Absyn.ArrayTy)e);
    } else {
      throw new Error("transTy");
    }
  }

  ExpTy transExp(Absyn.VarExp e) {
    return transVar(e.var);
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
    Object o = env.venv.get(e.func);

    if (o == null) {
      Error(e.pos, "undefined function " + e.func);
    } else if (!(o instanceof FunEntry)) {
      Error(e.pos, e.func + " is not a function");
    } else {
      FunEntry f = (FunEntry)o;
      Types.RECORD param = f.formals;
      Absyn.ExpList arg = e.args;

      while (param != null && arg != null) {
        ExpTy et = transExp(arg.head);
        if (!et.ty.coerceTo(param.fieldType)) {
          Error(arg.head.pos, "transExp(Absyn.CallExp)");
        }
        param = param.tail;
        arg = arg.tail;
      }

      if (param != null || arg != null) {
        Error(e.pos, "Argument/Parameter count mismatch.");
      }

      return new ExpTy(null, f.result.actual());
    }

    return new ExpTy(null, INT);
  }

  ExpTy transExp(Absyn.OpExp e) {
    ExpTy left = transExp(e.left);
    ExpTy right = transExp(e.right);

    switch (e.oper) {
      case Absyn.OpExp.PLUS:
      case Absyn.OpExp.MINUS:
      case Absyn.OpExp.MUL:
      case Absyn.OpExp.DIV:
        checkInt(left,e.left.pos);
        checkInt(right,e.right.pos);
        return new ExpTy(null,INT);
      case Absyn.OpExp.EQ:
      case Absyn.OpExp.NE:
        if (left.ty.coerceTo(INT) && right.ty.coerceTo(INT))
          return new ExpTy(null,INT);
        if (left.ty.coerceTo(STRING) && right.ty.coerceTo(STRING))
          return new ExpTy(null,INT);
        if (left.ty.coerceTo(right.ty) && right.ty.actual() instanceof Types.ARRAY)
          return new ExpTy(null,INT);
        if (left.ty.coerceTo(right.ty) && right.ty.actual() instanceof Types.RECORD)
          return new ExpTy(null,INT);
        if (left.ty.coerceTo(NIL) && right.ty.actual() instanceof Types.RECORD)
          return new ExpTy(null,INT);
        if (left.ty.actual() instanceof Types.RECORD && right.ty.coerceTo(NIL))
          return new ExpTy(null,INT);
        Error(e.left.pos, "Type ERROR! in OpExp of EQ and NE");
        return new ExpTy(null,INT);
      case Absyn.OpExp.LT:
      case Absyn.OpExp.LE:
      case Absyn.OpExp.GT:
      case Absyn.OpExp.GE:
        if ((left.ty instanceof Types.INT) && (right.ty instanceof Types.INT)) {
          return new ExpTy(null, INT);
        } else if ((left.ty instanceof Types.STRING) && (right.ty instanceof Types.STRING)) {
          return new ExpTy(null, INT);
        }
        break;
      default:
        throw new Error("transExp.OpExp - unknown operator");
    }

    return null;
  }

  ExpTy transExp(Absyn.RecordExp e) {
    Type t = (Type)env.tenv.get(e.typ);
    if (t == null) {
      Error(e.pos, "undefined type \"" + e.typ + "\"");
      return new ExpTy(null, INT);
    } else if (!(t instanceof Types.RECORD)) {
      Error(e.pos, "\"" + e.typ + "\" is not a record type");
      return new ExpTy(null, INT);
    }

    Absyn.FieldExpList field = e.fields;
    Types.RECORD record = (Types.RECORD)t;
    
    while (field != null && record != null) {
      if (field.name != record.fieldName) {
        Error(field.pos, "field name doesn't match");
        return new ExpTy(null, INT);
      }
      
      ExpTy et = transExp(field.init);
      if (!et.ty.coerceTo(record.fieldType)) {
        Error(field.init.pos, "field type doesn't match");
        return new ExpTy(null, INT);
      }
      
      field = field.tail;
      record = record.tail;
    }
    
    if (field != null || record != null) {
      Error(e.pos, "field count doesn't match");
      return new ExpTy(null, INT);
    }
    
    return new ExpTy(null, t);
  }

  ExpTy transExp(Absyn.SeqExp e) {
    System.out.println("transExp(Absyn.SeqExp)");
    System.out.println(e.toString());
    Type type = new Types.VOID();

    for (Absyn.ExpList list = e.list; list != null; list = list.tail) {
      ExpTy et = transExp(list.head);
      type = et.ty;
    }

    return new ExpTy(null, type.actual());
  }

  ExpTy transExp(Absyn.AssignExp e) {
    ExpTy left = transVar(e.var);
    ExpTy right = transExp(e.exp);
    if (e.var instanceof Absyn.SimpleVar && env.venv.get(((Absyn.SimpleVar)e.var).name) instanceof LoopVarEntry)
      Error(e.pos, "Loop value should not be assigned");
    if (!(right.ty.coerceTo(left.ty)))
      Error(e.pos,"wrong assign value");
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.IfExp e) {
                // TODO
    return null;
  }

  ExpTy transExp(Absyn.WhileExp e) {
    ExpTy test = transExp(e.test);
    checkInt(test, e.test.pos);
    //LoopSemant ls = new LoopSemant(env);
    LOOPNUM++;
    ExpTy body = transExp(e.body);
    if ( !(body.ty.actual() instanceof Types.VOID) )
      Error(e.body.pos, "Body of WHILE cannot return any value.");
    LOOPNUM--;
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.ForExp e) {
    ExpTy low = transExp(e.var.init);
    checkInt(low, e.var.pos);
    ExpTy high = transExp(e.hi);
    checkInt(high, e.hi.pos);
    env.venv.beginScope();
    //Access access = level.allocLocal(e.var.escape);
    VarEntry entry = new LoopVarEntry(INT);
    env.venv.put(e.var.name, entry);
    LOOPNUM++;
    //LoopSemant ls = new LoopSemant(env);
    ExpTy body = transExp(e.body);
    env.venv.endScope();
    if ( !(body.ty.actual() instanceof Types.VOID) )
    {
      Error(e.body.pos, "Body of FOR cannot return any value.");
    }
    LOOPNUM--;
    return new ExpTy(null, VOID);
  }

  ExpTy transExp(Absyn.BreakExp e) {
    if (LOOPNUM <= 0)
      Error(e.pos, "Break must be in loop.");
    return new ExpTy(null,VOID);
  }

  ExpTy transExp(Absyn.LetExp e)
  {
    env.venv.beginScope();
    env.tenv.beginScope();
    //Translate.ExpList el = new Translate.ExpList(null, null), el1 = el;
    for ( Absyn.DecList p = e.decs; p != null; p = p.tail )
    {
      //el1 = el1.tail = new Translate.ExpList(transDec(p.head), null);
      transDec(p.head);
    }
    ExpTy et = transExp(e.body);
    env.tenv.endScope();
    env.venv.endScope();
    return new ExpTy(null, et.ty.actual());
  }

  ExpTy transExp(Absyn.ArrayExp e) {
    Type type = (Type)env.tenv.get(e.typ);
    if (type == null) {
      Error(e.pos, "Undefined array type");
      return new ExpTy(null, INT);
    }
    
    type = type.actual();
    if (!(type instanceof Types.ARRAY)) {
      Error(e.pos, "Array type required");
      return new ExpTy(null, INT);
    }
    
    ExpTy arraySize = transExp(e.size);
    checkInt(arraySize, e.size.pos);
    
    ExpTy init = transExp(e.init);
    if (!(init.ty.coerceTo(((Types.ARRAY)type).element.actual())))
       Error(e.pos,"type do not match of the array");
    
    if (!init.ty.actual().coerceTo(INT)) {
      return new ExpTy(null, type);
    }
    else
      return new ExpTy(null, type);
  }
}