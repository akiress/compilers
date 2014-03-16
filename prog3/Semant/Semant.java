package Semant;
import Translate.Exp;
import Types.Type;
import java.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

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

  public static String spyFields(Object obj) {
    try {
        StringBuffer buffer = new StringBuffer();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
          if (!Modifier.isStatic(f.getModifiers())) {
            f.setAccessible(true);
            Object value = f.get(obj);
            buffer.append(f.getType().getName());
            buffer.append(" ");
            buffer.append(f.getName());
            buffer.append("=");
            buffer.append("" + value);
            buffer.append("\n");
          }
        }
        return buffer.toString();
      } catch (IllegalAccessException exc) {
        System.err.println(exc);
        return null;
      }
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
                                                             
//                                                               
//  DDDDDDDDDDDDD                                                
//  D::::::::::::DDD                                             
//  D:::::::::::::::DD                                           
//  DDD:::::DDDDD:::::D                                          
//    D:::::D    D:::::D     eeeeeeeeeeee        cccccccccccccccc
//    D:::::D     D:::::D  ee::::::::::::ee    cc:::::::::::::::c
//    D:::::D     D:::::D e::::::eeeee:::::ee c:::::::::::::::::c
//    D:::::D     D:::::De::::::e     e:::::ec:::::::cccccc:::::c
//    D:::::D     D:::::De:::::::eeeee::::::ec::::::c     ccccccc
//    D:::::D     D:::::De:::::::::::::::::e c:::::c             
//    D:::::D     D:::::De::::::eeeeeeeeeee  c:::::c             
//    D:::::D    D:::::D e:::::::e           c::::::c     ccccccc
//  DDD:::::DDDDD:::::D  e::::::::e          c:::::::cccccc:::::c
//  D:::::::::::::::DD    e::::::::eeeeeeee   c:::::::::::::::::c
//  D::::::::::::DDD       ee:::::::::::::e    cc:::::::::::::::c
//  DDDDDDDDDDDDD            eeeeeeeeeeeeee      cccccccccccccccc
//

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

  Types.RECORD transTypeFields(Absyn.FieldList l){
    if(l==null)
      return null;
    Types.RECORD r=new Types.RECORD(l.name,(Type)env.tenv.get(l.typ),null);
    Types.RECORD tmp=r;
    for(Absyn.FieldList f=l.tail;f!=null;f=f.tail,r=r.tail)
      r.tail=new Types.RECORD(f.name,(Type)env.tenv.get(f.typ),null);
    return tmp;
  }

  Exp transDec(Absyn.TypeDec d) {
    List <Symbol.Symbol> list = new ArrayList <Symbol.Symbol> ();
    for (Absyn.TypeDec now = d; now != null; now = now.next)
      if (list.contains(now.name))
        Error(now.pos, "Already defined.");
      else {
        list.add(now.name);
        env.tenv.put(now.name, new Types.NAME(now.name));
      }
    for (Absyn.TypeDec now = d; now != null; now = now.next)
      ((Types.NAME)env.tenv.get(now.name)).bind(transTy(now.ty));
    for (Absyn.TypeDec now = d; now != null; now = now.next)
      if (((Types.NAME)env.tenv.get(now.name)).isLoop())
        Error(now.pos, "Loop declaration");
    return null;
  }
  
  Exp transDec(Absyn.VarDec d) {
    System.out.println("In VarDec");
    ExpTy init = transExp(d.init);
    Type ty = null;
    if (d.typ == null)
      if (init.ty == NIL) {
        Error(d.init.pos, "Vairiable initialization error.");
        return null;
      } else
        ty = init.ty;
    else {
      ty = transTy(d.typ).actual();
      if (!(init.ty.coerceTo(ty)))
        Error(d.pos, "Type mismatches.");
    }
    env.venv.put(d.name, new VarEntry(ty));
    return null;
  }

  Exp transDec(Absyn.FunctionDec d){
    ArrayList<String> list =new ArrayList<String>();
    env.venv.beginScope();
    Type re=transTy(d.result);
    for(Absyn.FieldList f=d.params;f!=null;f=f.tail){
      if(list.contains(f.name.toString()))
        Error(d.pos, "function parameter '"+f.name+"' has been already defined ");
      list.add(f.name.toString());
      Type ty=(Type)env.tenv.get(f.typ);
      if(ty==null)
        Error(f.pos, "undefined type '"+f.typ+"'");
      env.venv.put(f.name, new VarEntry(ty));
    }
    ExpTy et=transExp(d.body);
    if(!(re.actual().coerceTo(et.ty.actual()) || re.actual() instanceof Types.RECORD && et.ty.actual() instanceof Types.NIL))
      Error(d.pos, "function return type is not matched");
    env.venv.endScope();
    return null;
  }
                                                                 
//                                                                   
//  VVVVVVVV           VVVVVVVV                                      
//  V::::::V           V::::::V                                      
//  V::::::V           V::::::V                                      
//  V::::::V           V::::::V                                      
//   V:::::V           V:::::V   aaaaaaaaaaaaa   rrrrr   rrrrrrrrr   
//    V:::::V         V:::::V    a::::::::::::a  r::::rrr:::::::::r  
//     V:::::V       V:::::V     aaaaaaaaa:::::a r:::::::::::::::::r 
//      V:::::V     V:::::V               a::::a rr::::::rrrrr::::::r
//       V:::::V   V:::::V         aaaaaaa:::::a  r:::::r     r:::::r
//        V:::::V V:::::V        aa::::::::::::a  r:::::r     rrrrrrr
//         V:::::V:::::V        a::::aaaa::::::a  r:::::r            
//          V:::::::::V        a::::a    a:::::a  r:::::r            
//           V:::::::V         a::::a    a:::::a  r:::::r            
//            V:::::V          a:::::aaaa::::::a  r:::::r            
//             V:::V            a::::::::::aa:::a r:::::r            
//              VVV              aaaaaaaaaa  aaaa rrrrrrr            
//                                                                                                                                     

  ExpTy transVar(Absyn.Var e) {
    if (e instanceof Absyn.SimpleVar) {
      return transVar((Absyn.SimpleVar)e);
    } else if (e instanceof Absyn.FieldVar) {
      return transVar((Absyn.FieldVar)e);
    } else if (e instanceof Absyn.SubscriptVar) {
      return transVar((Absyn.SubscriptVar)e);
    } else {
      throw new Error("Variable error.");
    }
  }

  ExpTy transVar(Absyn.SimpleVar v) {
    Entry x = (Entry)env.venv.get(v.name);      
    if(x instanceof VarEntry) {
      VarEntry ent = (VarEntry)x;
      return new ExpTy(null, ent.ty);
    } else {
      Error(v.pos, "Error in variable " + '\"' + v.name + '\"');
    }
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transVar(Absyn.SubscriptVar v) {
    ExpTy array = transVar(v.var);
    if (!(array.ty.actual() instanceof Types.ARRAY)) {
      Error(v.pos, "ARRAY needed");
      return new ExpTy(null, VOID);
    }
    ExpTy index = transExp(v.index);
    checkInt(index, v.pos);
    return new ExpTy(null, ((Types.ARRAY)array.ty).element.actual());
  }

  ExpTy transVar(Absyn.FieldVar v) {
    ExpTy l = transVar(v.var);
    int offset = 0;
    if (l.ty instanceof Types.RECORD) {
      Types.RECORD rec;
      for (rec = (Types.RECORD)l.ty; rec != null; rec = rec.tail, offset++)
        if (rec.fieldName.toString() == v.field.toString())
          break;
      if (rec == null) {
        Error(v.pos, "Field " + v.field.toString() + " does not exist");
        return new ExpTy(null, INT);
      }
      else
        return new ExpTy(null, rec.fieldType.actual());
    }
    else {
      Error(v.pos, "Record type required");
      return new ExpTy(null, INT);
    }
  }
                                                  
//                                                  
//  TTTTTTTTTTTTTTTTTTTTTTT                         
//  T:::::::::::::::::::::T                         
//  T:::::::::::::::::::::T                         
//  T:::::TT:::::::TT:::::T                         
//  TTTTTT  T:::::T  TTTTTTyyyyyyy           yyyyyyy
//          T:::::T         y:::::y         y:::::y 
//          T:::::T          y:::::y       y:::::y  
//          T:::::T           y:::::y     y:::::y   
//          T:::::T            y:::::y   y:::::y    
//          T:::::T             y:::::y y:::::y     
//          T:::::T              y:::::y:::::y      
//          T:::::T               y:::::::::y       
//        TT:::::::TT              y:::::::y        
//        T:::::::::T               y:::::y         
//        T:::::::::T              y:::::y          
//        TTTTTTTTTTT             y:::::y           
//                               y:::::y            
//                              y:::::y             
//                             y:::::y              
//                            y:::::y               
//                           yyyyyyy                
//                                              

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
  
  Type transTy(Absyn.NameTy t) {
    Type type = (Type) (env.tenv.get(t.name));
    if (type != null)
      return type;
    else {
      Error(t.pos, "Undefined type " + t.name.toString());
      return INT;
    }
  }

  Type transTy(Absyn.RecordTy t){
    Types.RECORD result = null, tmp = null;
    if(env.tenv.get(t.fields.typ) == null)
      env.errorMsg.error(t.pos, "undefined type '"+t.fields.typ+"'");
    result = new Types.RECORD(t.fields.name,(Type)env.tenv.get(t.fields.typ),null);
    ArrayList<String> list = new ArrayList<String>();
    list.add(t.fields.name.toString());
    tmp = result;
    env.venv.beginScope();
    for(Absyn.FieldList f=t.fields.tail;f!=null;f=f.tail){
      if(list.contains(f.name.toString()))
        env.errorMsg.error(t.pos, "feild '"+f.name+"' has been already defined");
      else 
        list.add(f.name.toString());
      Type p=(Type)env.tenv.get(f.typ);
      if(p==null)
        env.errorMsg.error(t.pos, "undefined type '"+f.typ+"'");
      env.venv.put(f.name, new VarEntry(p));
      tmp.tail=new Types.RECORD(f.name,p,null);
      tmp=tmp.tail;
    }
    env.venv.endScope();
    return result;
  }

  Type transTy(Absyn.ArrayTy t) {
    Type tType = (Type) (env.tenv.get(t.typ));
    if (tType == null) {
      Error(t.pos, "Undefined type " + t.typ.toString());
      return INT;
    }
    return new Types.ARRAY(tType);
  }
                                                              
//                                                                
//  EEEEEEEEEEEEEEEEEEEEEE                                        
//  E::::::::::::::::::::E                                        
//  E::::::::::::::::::::E                                        
//  EE::::::EEEEEEEEE::::E                                        
//    E:::::E       EEEEEExxxxxxx      xxxxxxxppppp   ppppppppp   
//    E:::::E              x:::::x    x:::::x p::::ppp:::::::::p  
//    E::::::EEEEEEEEEE     x:::::x  x:::::x  p:::::::::::::::::p 
//    E:::::::::::::::E      x:::::xx:::::x   pp::::::ppppp::::::p
//    E:::::::::::::::E       x::::::::::x     p:::::p     p:::::p
//    E::::::EEEEEEEEEE        x::::::::x      p:::::p     p:::::p
//    E:::::E                  x::::::::x      p:::::p     p:::::p
//    E:::::E       EEEEEE    x::::::::::x     p:::::p    p::::::p
//  EE::::::EEEEEEEE:::::E   x:::::xx:::::x    p:::::ppppp:::::::p
//  E::::::::::::::::::::E  x:::::x  x:::::x   p::::::::::::::::p 
//  E::::::::::::::::::::E x:::::x    x:::::x  p::::::::::::::pp  
//  EEEEEEEEEEEEEEEEEEEEEExxxxxxx      xxxxxxx p::::::pppppppp    
//                                             p:::::p            
//                                             p:::::p            
//                                            p:::::::p           
//                                            p:::::::p           
//                                            p:::::::p           
//                                            ppppppppp           
//                                                                

  ExpTy transExp(Absyn.Exp e) {
    ExpTy result;

    if (e instanceof Absyn.VarExp) result = transExp((Absyn.VarExp)e);
    if (e instanceof Absyn.NilExp) result = transExp((Absyn.NilExp)e);
    if (e instanceof Absyn.IntExp) result = transExp((Absyn.IntExp)e);
    if (e instanceof Absyn.StringExp) result = transExp((Absyn.StringExp)e);
    if (e instanceof Absyn.CallExp) result = transExp((Absyn.CallExp)e);
    if (e instanceof Absyn.OpExp) result = transExp((Absyn.OpExp)e);
    if (e instanceof Absyn.RecordExp) result = transExp((Absyn.RecordExp)e);
    if (e instanceof Absyn.SeqExp) result = transExp((Absyn.SeqExp)e);
    if (e instanceof Absyn.AssignExp) result = transExp((Absyn.AssignExp)e);
    if (e instanceof Absyn.IfExp) result = transExp((Absyn.IfExp)e);
    if (e instanceof Absyn.WhileExp) result = transExp((Absyn.WhileExp)e);
    if (e instanceof Absyn.ForExp) result = transExp((Absyn.ForExp)e);
    if (e instanceof Absyn.BreakExp) result = transExp((Absyn.BreakExp)e);
    if (e instanceof Absyn.LetExp) result = transExp((Absyn.LetExp)e);
    if (e instanceof Absyn.ArrayExp) result = transExp((Absyn.ArrayExp)e);
    return new ExpTy(null, new Types.VOID());

  }

  ExpTy transExp(Absyn.IntExp e) {
    return new ExpTy(null, INT);
  }

  ExpTy transExp(Absyn.StringExp e) {
    return new ExpTy(null, STRING);
  }
  
  ExpTy transExp(Absyn.NilExp e) {
    return new ExpTy(null, NIL);
  }
  
  ExpTy transExp(Absyn.VarExp e) {
    return transVar(e.var);
  }


  ExpTy transExp(Absyn.SeqExp e) {
    Type type = new Types.VOID();
    for (Absyn.ExpList list = e.list; list != null; list = list.tail) {
      ExpTy et = transExp(list.head);
      type = et.ty;
    }

    return new ExpTy(null, type.actual());
  }

  ExpTy transExp(Absyn.ArrayExp e) {
    Type type = (Type)env.tenv.get(e.typ);
    if (type == null) {
      Error(e.pos, "Undefined array type.");
      return new ExpTy(null, INT);
    }
    
    type = type.actual();
    if (!(type instanceof Types.ARRAY)) {
      Error(e.pos, "Array type required.");
      return new ExpTy(null, INT);
    }
    
    ExpTy arraySize = transExp(e.size);
    checkInt(arraySize, e.size.pos);
    
    ExpTy init = transExp(e.init);
    if (!(init.ty.coerceTo(((Types.ARRAY)type).element.actual())))
       Error(e.pos, "Mismatched array types.");
    
    if (!init.ty.actual().coerceTo(INT)) {
      return new ExpTy(null, type);
    }
    else
      return new ExpTy(null, type);
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

  ExpTy transExp(Absyn.OpExp e) {
    ExpTy left = transExp(e.left);
    ExpTy right = transExp(e.right);
    switch (e.oper) {
      case Absyn.OpExp.PLUS:
      case Absyn.OpExp.MINUS:
      case Absyn.OpExp.MUL:
      case Absyn.OpExp.DIV:
        if (!((left.ty instanceof Types.INT) && (right.ty instanceof Types.INT)))
          Error(e.pos, "Illegal operator. Integer required on both sides.");
        return new ExpTy(null, INT);
      case Absyn.OpExp.EQ:
      case Absyn.OpExp.NE:
        if (left.ty.coerceTo(INT) && right.ty.coerceTo(INT))
          return new ExpTy(null, INT);
        if (left.ty.coerceTo(STRING) && right.ty.coerceTo(STRING))
          return new ExpTy(null, INT);
        if (left.ty.coerceTo(right.ty) && right.ty.actual() instanceof Types.ARRAY)
          return new ExpTy(null, INT);
        if (left.ty.coerceTo(right.ty) && right.ty.actual() instanceof Types.RECORD)
          return new ExpTy(null, INT);
        if (left.ty.coerceTo(NIL) && right.ty.actual() instanceof Types.RECORD)
          return new ExpTy(null, INT);
        if (left.ty.actual() instanceof Types.RECORD && right.ty.coerceTo(NIL))
          return new ExpTy(null, INT);
        Error(e.left.pos, "EQ/NE Error.");
        return new ExpTy(null, INT);
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

  ExpTy transExp(Absyn.AssignExp e){
    ExpTy v=transVar(e.var);
    ExpTy t=transExp(e.exp);
    if(v.ty instanceof Types.NAME)
      v.ty=v.ty.actual();
    if(t.ty instanceof Types.NAME)
      t.ty=t.ty.actual();
    if(!(v.ty.coerceTo(t.ty) || (v.ty instanceof Types.RECORD && t.ty instanceof Types.NIL))){
      String t1=t.ty.actual().toString();
      System.out.print(t1);
      String t2=v.ty.actual().toString();
      Error(e.pos, "Type "+t1.substring(t1.indexOf('.')+1, t1.indexOf('@'))+
             " can't be assigned to "+t2.substring(t2.indexOf('.')+1, t2.indexOf('@')));
    }
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transExp(Absyn.BreakExp e) {
    if (LOOPNUM <= 0)
      Error(e.pos, "Break must be in loop.");
    return new ExpTy(null, VOID);
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

  ExpTy transExp(Absyn.LetExp e)
  {
    env.venv.beginScope();
    env.tenv.beginScope();
    for ( Absyn.DecList p = e.decs; p != null; p = p.tail )
    {
      transDec(p.head);
    }
    ExpTy et = transExp(e.body);
    env.tenv.endScope();
    env.venv.endScope();
    return new ExpTy(null, et.ty.actual());
  }

  ExpTy transExp(Absyn.IfExp e) {
    ExpTy test = transExp(e.test);
    ExpTy thenclause = transExp(e.thenclause);
    ExpTy elseclause = e.elseclause == null ? null : transExp(e.elseclause);
    if (!(test.ty instanceof Types.INT)) {
      Error(e.test.pos, "integer expression expected");
      return new ExpTy(null, elseclause == null ? VOID : INT);
    }
    if (elseclause == null) {
      if (!(thenclause.ty instanceof Types.VOID)) {
        Error(e.thenclause.pos, "void expression expected");
        return new ExpTy(null, VOID);
      }
      return new ExpTy(null, VOID);
    } else {
      if (elseclause.ty.coerceTo(thenclause.ty)) {
        return new ExpTy(null, thenclause.ty);
      } else if (thenclause.ty.coerceTo(elseclause.ty)) {
        return new ExpTy(null, elseclause.ty);
      } else {
        Error(e.elseclause.pos, "incompatible types between then and else clause");
        return new ExpTy(null, INT);
      }
    }
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
    VarEntry entry = new LoopVarEntry(INT);
    env.venv.put(e.var.name, entry);
    LOOPNUM++;
    ExpTy body = transExp(e.body);
    env.venv.endScope();
    if ( !(body.ty.actual() instanceof Types.VOID) )
    {
      Error(e.body.pos, "Body of FOR cannot return any value.");
    }
    LOOPNUM--;
    return new ExpTy(null, VOID);
  }
}