package FindEscape;

public class FindEscape {
  Symbol.Table escEnv = new Symbol.Table(); // escEnv maps Symbol to Escape
  private Absyn.FunctionDec currentFun = null;
  
  public FindEscape(Absyn.Exp e) { traverseExp(0, e);  }

/* traverseVar */
  void traverseVar(int depth, Absyn.Var v) {
    if (v instanceof Absyn.FieldVar)
      traverseVar(depth, (Absyn.FieldVar)v);
    else if (v instanceof Absyn.SimpleVar)
      traverseVar(depth, (Absyn.SimpleVar)v);
    else if (v instanceof Absyn.SubscriptVar)
      traverseVar(depth, (Absyn.SubscriptVar)v);
    else
      throw new Error("FindEscape.transverseVar");
  }
  
  void traverseVar(int depth, Absyn.FieldVar v) {
    traverseVar(depth, v.var);
  }
  
  void traverseVar(int depth, Absyn.SimpleVar v) {
    Escape esc = (Escape)escEnv.get(v.name);
    if ((esc != null) && (esc.depth < depth))
      esc.setEscape();
  }
  
  void traverseVar(int depth, Absyn.SubscriptVar v) {
    traverseVar(depth, v.var);
    traverseExp(depth, v.index);
  }

/* traverseExp */
  void traverseExp(int depth, Absyn.Exp e) {
    if (e instanceof Absyn.ArrayExp)
      traverseExp(depth, (Absyn.ArrayExp)e);
    else if (e instanceof Absyn.AssignExp)
      traverseExp(depth, (Absyn.AssignExp)e);
    else if (e instanceof Absyn.CallExp)
      traverseExp(depth, (Absyn.CallExp)e);
    else if (e instanceof Absyn.ForExp)
      traverseExp(depth, (Absyn.ForExp)e);
    else if (e instanceof Absyn.IfExp)
      traverseExp(depth, (Absyn.IfExp)e);
    else if (e instanceof Absyn.LetExp)
      traverseExp(depth, (Absyn.LetExp)e);
    else if (e instanceof Absyn.OpExp)
      traverseExp(depth, (Absyn.OpExp)e);
    else if (e instanceof Absyn.RecordExp)
      traverseExp(depth, (Absyn.RecordExp)e);
    else if (e instanceof Absyn.SeqExp)
      traverseExp(depth, (Absyn.SeqExp)e);
    else if (e instanceof Absyn.VarExp)
      traverseExp(depth, (Absyn.VarExp)e);
    else if (e instanceof Absyn.WhileExp)
      traverseExp(depth, (Absyn.WhileExp)e);
  }
  
  void traverseExp(int depth, Absyn.ArrayExp e) {
    traverseExp(depth, e.size);
    traverseExp(depth, e.init);
  }
  
  void traverseExp(int depth, Absyn.AssignExp e) {
    traverseVar(depth, e.var);
    traverseExp(depth, e.exp);
  }
  
  void traverseExp(int depth, Absyn.CallExp e) {
    if (currentFun != null)
      currentFun.leaf = false;
    for(Absyn.ExpList arg = e.args; arg != null; arg = arg.tail)
      traverseExp(depth, arg.head);
  }
  
  void traverseExp(int depth, Absyn.ForExp e) {
    traverseExp(depth, e.var.init);
    traverseExp(depth, e.hi);
    escEnv.beginScope();
    escEnv.put(e.var.name, new VarEscape(depth, e.var));
    traverseExp(depth, e.body);
    escEnv.endScope();
  }
  
  void traverseExp(int depth, Absyn.IfExp e) {
    traverseExp(depth, e.test);
    traverseExp(depth, e.thenclause);
    traverseExp(depth, e.elseclause);
  }
  
  void traverseExp(int depth, Absyn.LetExp e) {
    escEnv.beginScope();
    for (Absyn.DecList dec = e.decs; dec != null; dec = dec.tail)
      traverseDec(depth, dec.head);
    traverseExp(depth, e.body);
    escEnv.endScope();
  }
  
  void traverseExp(int depth, Absyn.OpExp e) {
    traverseExp(depth, e.left);
    traverseExp(depth, e.right);
  }
  
  void traverseExp(int depth, Absyn.RecordExp e) {
    for (Absyn.FieldExpList field = e.fields; field != null; field = field.tail)
      traverseExp(depth, field.init);
  }
  
  void traverseExp(int depth, Absyn.SeqExp e) {
    for (Absyn.ExpList exp = e.list; exp != null; exp = exp.tail)
      traverseExp(depth, exp.head);
  }
  
  void traverseExp(int depth, Absyn.VarExp e) {
    traverseVar(depth, e.var);
  }
  
  void traverseExp(int depth, Absyn.WhileExp e) {
    traverseExp(depth, e.test);
    traverseExp(depth, e.body);
  }

/* traverseDec */
  void traverseDec(int depth, Absyn.Dec d) {    
    if (d instanceof Absyn.FunctionDec)
      traverseDec(depth, (Absyn.FunctionDec)d);
    else if (d instanceof Absyn.VarDec)
      traverseDec(depth, (Absyn.VarDec)d);
  }
  
  void traverseDec(int depth, Absyn.FunctionDec d) {
    Absyn.FunctionDec priorFun = currentFun;
    for (Absyn.FunctionDec dec = d; dec != null; dec = dec.next) {
      escEnv.beginScope();
      currentFun = dec;
      for (Absyn.FieldList param = dec.params; param != null; param = param.tail)
        escEnv.put(param.name, new FormalEscape(depth + 1, param));
      traverseExp(depth + 1, dec.body);
      escEnv.endScope();
    }
    currentFun = priorFun;
  }
  
  void traverseDec(int depth, Absyn.VarDec d) {
    traverseExp(depth, d.init);
    escEnv.put(d.name, new VarEscape(depth, d));
  }
}
