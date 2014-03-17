package Semant;
import Translate.Exp;
import Types.Type;
import java.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

public class Semant {
  Env env;

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
    if (e instanceof Absyn.FunctionDec) return transDec((Absyn.FunctionDec)e);
    if (e instanceof Absyn.VarDec) return transDec((Absyn.VarDec)e);
    if (e instanceof Absyn.TypeDec) return transDec((Absyn.TypeDec)e);
    return null;
  }

  Exp transDec(Absyn.TypeDec d){
    if (((Types.NAME)env.tenv.get(d.name)).isLoop()) {
      Error(d.pos, "No definition for " + d.name);
    }
    return null;
  }
  
  Exp transDec(Absyn.VarDec d) {
    ExpTy var = transExp(d.init);
    if (d.typ != null) {
      Type dec = transTy(d.typ);
      if (dec instanceof Types.NAME) dec = dec.actual();
      if (!var.ty.coerceTo(dec))
        if(!((dec instanceof Types.RECORD) && var.ty instanceof Types.NIL) )
          Error(d.pos, "Initailizing type is not matched");
    } else {
      if (var.ty instanceof Types.NIL) 
        env.errorMsg.error(d.pos, "Nil initialization of " + d.name.toString());
    }
    env.venv.put(d.name, new VarEntry(var.ty));
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
                                                                 
  Types.RECORD transTypeFields(Absyn.FieldList fl){
    if (fl == null) return null;
    Types.RECORD rec = new Types.RECORD(fl.name, (Type)env.tenv.get(fl.typ), null);
    Types.RECORD result = rec;
    for (Absyn.FieldList field = fl.tail; field != null; field = field.tail, rec = rec.tail)
      rec.tail = new Types.RECORD(field.name, (Type)env.tenv.get(field.typ), null);
    return result;
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
    if (e instanceof Absyn.SimpleVar) return transVar((Absyn.SimpleVar)e);
    if (e instanceof Absyn.FieldVar) return transVar((Absyn.FieldVar)e);
    if (e instanceof Absyn.SubscriptVar) return transVar((Absyn.SubscriptVar)e);
    Error(e.pos, "Variable error.");
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transVar(Absyn.SimpleVar v) {
    System.out.println(v.name);
    Entry x = (Entry)env.venv.get(v.name);      
    if(x instanceof VarEntry) {
      VarEntry ent = (VarEntry)x;
      return new ExpTy(null, ent.ty);
    } else {
      Error(v.pos, "Error in variable " + '\"' + v.name + '\"');
    }
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transVar(Absyn.FieldVar v) {
    ExpTy fvar = transExp(new Absyn.VarExp(v.pos, v.var));
    if (fvar.ty instanceof Types.NAME) {
      Types.RECORD field = (Types.RECORD)fvar.ty.actual();
      for(;field !=  null;field = field.tail) {
        if (field.fieldName.equals(v.field))
          break;
      }
      if (field == null) env.errorMsg.error(v.pos, "'"+v.field+"' is a member of "+v.var);
      else return new ExpTy(null, field.fieldType);
    }
    else env.errorMsg.error(v.pos,"'"+v.field+"' is not a valid field variable");
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transVar(Absyn.SubscriptVar v) {
    ExpTy sub = transExp(new Absyn.VarExp(v.pos, v.var));
    Type svar = sub.ty;
    if (svar instanceof Types.NAME) svar = svar.actual();
    if (!(svar instanceof Types.ARRAY)) env.errorMsg.error(v.pos, "The variable is not an array");
    ExpTy p = transExp(v.index);
    if (!(p.ty instanceof Types.INT)) env.errorMsg.error(v.pos, "The index must be an integer");
    if (!(svar instanceof Types.ARRAY)) return  new ExpTy(null, svar);
    else return new ExpTy(null,((Types.ARRAY)svar).element);
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
    if (e instanceof Absyn.NameTy) return transTy((Absyn.NameTy)e);
    if (e instanceof Absyn.RecordTy) return transTy((Absyn.RecordTy)e);
    if (e instanceof Absyn.ArrayTy) return transTy((Absyn.ArrayTy)e);
    Error(e.pos, "Type issue.");
    return null;
  }
  
  Type transTy(Absyn.NameTy t) {
    if (t == null) return new Types.VOID();
    Type nameTy = (Type)env.tenv.get(t.name);
    return nameTy;
  }

  Type transTy(Absyn.RecordTy t){
    Types.RECORD result = null, tmp = null;
    if (env.tenv.get(t.fields.typ) == null) Error(t.pos, "undefined type '"+t.fields.typ+"'");
    result = new Types.RECORD(t.fields.name,(Type)env.tenv.get(t.fields.typ),null);
    ArrayList<String> list = new ArrayList<String>();
    list.add(t.fields.name.toString());
    tmp = result;
    env.venv.beginScope();
    for(Absyn.FieldList fieldList = t.fields.tail; fieldList != null; fieldList = fieldList.tail) {
      if (list.contains(fieldList.name.toString())) Error(t.pos, "Redefinition of field " + fieldList.name);
      else list.add(fieldList.name.toString());
      Type p=(Type)env.tenv.get(fieldList.typ);
      if (p==null) Error(t.pos, "undefined type '"+fieldList.typ+"'");
      env.venv.put(fieldList.name, new VarEntry(p));
      tmp.tail = new Types.RECORD(fieldList.name,p,null);
      tmp = tmp.tail;
    }
    env.venv.endScope();
    return result;
  }

  Type transTy(Absyn.ArrayTy t) {
    Type type = (Type) (env.tenv.get(t.typ));
    if (type == null) {
      Error(t.pos, "Undefined type " + t.typ.toString());
      return INT;
    }
    return new Types.ARRAY(type);
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
    return new ExpTy(null, new Types.INT());
  }

  ExpTy transExp(Absyn.StringExp e) {
    return new ExpTy(null, new Types.STRING());
  }
  
  ExpTy transExp(Absyn.NilExp e) {
    return new ExpTy(null, new Types.NIL());
  }
  
  ExpTy transExp(Absyn.VarExp e) {
    return transVar(e.var);
  }

  ExpTy transExp(Absyn.SeqExp e){
    if (e == null || e.list == null) return new ExpTy(null, new Types.VOID());
    ExpTy type = null;
    for (Absyn.ExpList seq = e.list; seq != null; seq = seq.tail) type = transExp(seq.head);
    return type;
  }

  ExpTy transExp(Absyn.ArrayExp e){
    ExpTy index = transExp(e.size);
    if (!(index.ty instanceof Types.INT)) Error(e.pos, "Index NAN.");
    Type type = (Type)env.tenv.get(e.typ);
    if (!(type instanceof Types.NAME) || !(type.actual() instanceof Types.ARRAY)){
      Error(e.pos, "Invalid array: " + e.typ);
      return index;
    }

    Type arr = type;
    if (type instanceof Types.NAME) arr = type.actual();
    if (arr instanceof Types.ARRAY) {
      if (!((Types.ARRAY)arr).element.coerceTo(transExp(e.init).ty))
        Error(e.pos, "Mismatched array types.");
    }

    return new ExpTy(null, arr);
  }
  
  ExpTy transExp(Absyn.RecordExp e){
    Type type = (Type)env.tenv.get(e.typ);
    if (type instanceof Types.NAME) {
      Types.RECORD rec = (Types.RECORD)type.actual();
      Absyn.FieldExpList f = e.fields;
      for(;rec != null && f != null; rec = rec.tail, f = f.tail){
        if (!f.name.equals(rec.fieldName)) Error(e.pos, "undefined field '" + f.name+"'");
        Type ftype = rec.fieldType;
        if (rec.fieldType instanceof Types.NAME) ftype=((Types.NAME)rec.fieldType).actual();
        if (!ftype.coerceTo(transExp(f.init).ty) && !(transExp(f.init).ty instanceof Types.NIL))
          Error(e.pos, "unmatched type '" + rec.fieldName + "'");
      }

      if (rec != null || f != null) Error(e.pos, "unmatched type");
      return new ExpTy(null, type);
    } else Error(e.pos, "undefined type '" + e.typ + "'");

    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transExp(Absyn.OpExp e) {
    ExpTy left = transExp(e.left);
    ExpTy right = transExp(e.right);
    switch (e.oper) {
      case Absyn.OpExp.PLUS:
      case Absyn.OpExp.MINUS:
      case Absyn.OpExp.MUL:
      case Absyn.OpExp.DIV:
        if (!(left.ty.coerceTo(INT) && right.ty.coerceTo(INT)))
          Error(e.pos, "Illegal operator. Integer required on both sides.");
        return new ExpTy(null, new Types.INT());
      case Absyn.OpExp.EQ:
      case Absyn.OpExp.NE:
      case Absyn.OpExp.LT:
      case Absyn.OpExp.LE:
      case Absyn.OpExp.GT:
      case Absyn.OpExp.GE:
        if (left.ty.coerceTo(INT) && right.ty.coerceTo(INT) ||
          (left.ty.coerceTo(STRING) && right.ty.coerceTo(STRING)) ||
          (left.ty.coerceTo(right.ty) && right.ty.actual() instanceof Types.ARRAY) ||
          (left.ty.coerceTo(right.ty) && right.ty.actual() instanceof Types.RECORD) ||
          (left.ty.coerceTo(NIL) && right.ty.actual() instanceof Types.RECORD) ||
          (left.ty.actual() instanceof Types.RECORD && right.ty.coerceTo(NIL)) ||
          ((left.ty instanceof Types.INT) && (right.ty instanceof Types.INT)) ||
          (left.ty instanceof Types.STRING) && (right.ty instanceof Types.STRING))
          Error(e.pos, "Comparison between non-alike types.");
        return new ExpTy(null, new Types.INT());
      default:
        Error(e.pos, "Illegal operator.");
        return new ExpTy(null, new Types.INT());
    }
  }

  ExpTy transExp(Absyn.AssignExp e){
    ExpTy var = transVar(e.var);
    if (var.ty instanceof Types.NAME) var.ty = var.ty.actual();
    if (env.venv.get(((Absyn.SimpleVar)e.var).name) instanceof LoopVarEntry) {
      Error(e.var.pos, "Loop variable cannot be assigned.");
      return new ExpTy(null, new Types.VOID());
    }
    ExpTy exp = transExp(e.exp);
    if (exp.ty instanceof Types.NAME) exp.ty = exp.ty.actual();
    if (!(var.ty.coerceTo(exp.ty) || (var.ty instanceof Types.RECORD && exp.ty instanceof Types.NIL))){
      Error(e.pos, "Type mismatch");
    }
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transExp(Absyn.BreakExp e) {
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transExp(Absyn.CallExp e) {
    Entry call = (Entry)env.venv.get(e.func);
    if (call == null) {
      Error(e.pos, "Undefined function: " + e.func);
      return new ExpTy(null, new Types.VOID());
    }
    if (call instanceof FunEntry) {
      FunEntry func = (FunEntry)call;
      Types.RECORD formals = func.formals;
      Absyn.ExpList explist = e.args;
      int index = 1;
      for(;formals!=null && explist!=null; formals = formals.tail, explist = explist.tail)
      {
        ExpTy type = transExp(explist.head);
        if (!type.ty.coerceTo(formals.fieldType))
          Error(e.pos, "Mismatch on parameter " + index);
        index++;
      }
      if (!(formals == null && explist == null)) Error(e.pos, "Function parameter mismatch.");
      return new ExpTy(null, func.result);
    } else {
      Error(e.pos, "Unknown call to function.");
      return new ExpTy(null, ((VarEntry)call).ty);
    }
  }

  ExpTy transExp(Absyn.LetExp e){
    env.venv.beginScope();
    env.tenv.beginScope();
    ArrayList<String> list = new ArrayList<String>();
    for(Absyn.DecList temp = e.decs; temp != null; temp = temp.tail) {
      Absyn.Dec head = temp.head;
      if (head instanceof Absyn.TypeDec){
        Absyn.TypeDec td=(Absyn.TypeDec)head;
        if (list.contains(td.name.toString()))
          env.errorMsg.error(td.pos, "type '"+td.name+"' has been already defined");
        list.add(td.name.toString());
        env.tenv.put(td.name,new Types.NAME(td.name));
      }
      if (head instanceof Absyn.FunctionDec){
        Absyn.FunctionDec fd=(Absyn.FunctionDec)head;
        Type result = transTy(fd.result);
        Types.RECORD rc = transTypeFields(fd.params);
        if (list.contains(fd.name.toString()))
          env.errorMsg.error(fd.pos, "function '"+fd.name+"' has been already defined");  
        else{
          list.add(fd.name.toString());
          env.venv.put(fd.name, new FunEntry(rc,result));
        }
      }
    }
    for(Absyn.DecList temp = e.decs; temp != null; temp = temp.tail) {
      Absyn.Dec head = temp.head;
      if (head instanceof Absyn.TypeDec) {
        Absyn.TypeDec td=(Absyn.TypeDec)head;
        ((Types.NAME)env.tenv.get(td.name)).bind(transTy(td.ty));
      }
    }
    list.clear();
    for(Absyn.DecList p = e.decs; p != null; p = p.tail)
      transDec(p.head);
    ExpTy et = transExp(e.body);
    env.venv.endScope();
    env.tenv.endScope();
    if (e.body == null)
      return new ExpTy(null, new Types.VOID());
    return new ExpTy(null, et.ty);
  }

  ExpTy transExp(Absyn.IfExp e) {
    ExpTy if_exp = transExp(e.test);
    if (!(if_exp.ty instanceof Types.INT)) Error(e.pos, "The test part must return an integer");
    ExpTy then = transExp(e.thenclause);
    if (e.elseclause != null)
    {
      ExpTy else_clause = transExp(e.elseclause);
      if (!then.ty.coerceTo(else_clause.ty)) {
        if (!(then.ty instanceof Types.NIL || else_clause.ty instanceof Types.NIL))
          Error(e.pos, "'if --then' expression must return same type");
      }
      return else_clause;
    }
    if (!(then.ty instanceof Types.VOID)) Error(e.pos, "'if --then' expression must return the type Types.VOID");
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transExp(Absyn.WhileExp e) {
    ExpTy while_exp = transExp(e.test);
    if (!(while_exp.ty instanceof Types.INT)) Error(e.pos, "The test part must return an integer");
    ExpTy t = transExp(e.body);
    if (!(t.ty instanceof Types.VOID)) Error(e.pos, "'while' expression must return the type Types.VOID");
    return new ExpTy(null, new Types.VOID());
  }

  ExpTy transExp(Absyn.ForExp e) {
    env.venv.beginScope();
    transDec(e.var);
    Entry for_exp = (Entry)env.venv.get(e.var.name);
    if (!(for_exp instanceof VarEntry)) Error(e.pos, "'"+e.var.name+"' isn't a variable");
    else {
      if (!(((VarEntry)for_exp).ty instanceof Types.INT))
        Error(e.pos, "'"+e.var.name+"' must be an integer");
    }
    ExpTy et = transExp(e.hi);
    if (!(et.ty instanceof Types.INT)) Error(e.pos, "An integer must follow 'to'");
    ExpTy ey = transExp(e.body);
    env.venv.endScope();
    if (!(ey.ty instanceof Types.VOID)) Error(e.pos, "'for' expression must return the type Types.VOID");
    return new ExpTy(null,new Types.VOID());
  }
}