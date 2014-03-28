package FindEscape;
public class FindEscape {
   static Symbol.Table escEnv = new Symbol.Table();
   static int dflag; // flag modalita' debug
   // avvia la ricorsione (reso obsoleto da traverseAbsyn)
   public FindEscape(Absyn.Exp e, int flag_debug) {
      dflag=flag_debug;
      traverseExp(0,e);
   }
   // metodo statico per l'etichettatura (escape) dell'albero della sintassi astratta
   public static void traverseAbsyn (Absyn.Exp e, int dflag) {
       FindEscape.dflag = dflag;
       traverseExp(0,e);
   }
   // traverseExp
   static void traverseExp(int depth, Absyn.Exp e) {
      if (e instanceof Absyn.ArrayExp)  traverseExp(depth,(Absyn.ArrayExp)e);
      if (e instanceof Absyn.AssignExp) traverseExp(depth,(Absyn.AssignExp)e);
      if (e instanceof Absyn.BreakExp)  traverseExp(depth,(Absyn.BreakExp)e);
      if (e instanceof Absyn.CallExp)   traverseExp(depth,(Absyn.CallExp)e);
      if (e instanceof Absyn.ForExp)    traverseExp(depth,(Absyn.ForExp)e);
      if (e instanceof Absyn.IfExp)     traverseExp(depth,(Absyn.IfExp)e);
      if (e instanceof Absyn.IntExp)    traverseExp(depth,(Absyn.IntExp)e);
      if (e instanceof Absyn.LetExp)    traverseExp(depth,(Absyn.LetExp)e);
      if (e instanceof Absyn.NilExp)    traverseExp(depth,(Absyn.NilExp)e);
      if (e instanceof Absyn.OpExp)     traverseExp(depth,(Absyn.OpExp)e);
      if (e instanceof Absyn.RecordExp) traverseExp(depth,(Absyn.RecordExp)e);
      if (e instanceof Absyn.SeqExp)    traverseExp(depth,(Absyn.SeqExp)e);
      if (e instanceof Absyn.StringExp) traverseExp(depth,(Absyn.StringExp)e);
      if (e instanceof Absyn.VarExp)    traverseExp(depth,(Absyn.VarExp)e);
      if (e instanceof Absyn.WhileExp)  traverseExp(depth,(Absyn.WhileExp)e);
   }
   // traverseVar
   static void traverseVar(int depth, Absyn.Var v) {
      if (v instanceof Absyn.SimpleVar) { traverseVar(depth,(Absyn.SimpleVar)v); }
      if (v instanceof Absyn.FieldVar)  { traverseVar(depth, ((Absyn.FieldVar)v).var); }
      if (v instanceof Absyn.SubscriptVar) {
          traverseVar(depth ,((Absyn.SubscriptVar)v).var);
          traverseExp(depth, ((Absyn.SubscriptVar)v).index);
      }
   }
   // SimpleVar
   static void traverseVar(int d,Absyn.SimpleVar v) {
      Escape x = (Escape)escEnv.get(v.name);
      if (x instanceof VarEscape) {
          VarEscape esc = (VarEscape)x;
          if (esc.depth < d) {
              esc.setEscape();
              if (dflag==1) System.out.println("   - [VarEscape] La variabile " + v.name + " e' di tipo escape");
          }
      }
      if (x instanceof FormalEscape) {
          FormalEscape esc = (FormalEscape)x;
          if (esc.depth < d)  {
              esc.setEscape();
              if (dflag==1) System.out.println("   - [FormalEscape] La variabile " + v.name + " e' di tipo escape");
          }
      }
   }
   // traverseDec
   static void traverseDec(int depth, Absyn.Dec d) {
      if(d instanceof Absyn.FunctionDec)  traverseDec(depth,(Absyn.FunctionDec)d);
      if(d instanceof Absyn.VarDec)       traverseDec(depth,(Absyn.VarDec)d);
   }
    // EXP
    static void traverseExp(int depth, Absyn.ExpList e) {
       for(;e!=null;e=e.tail) traverseExp(depth,e.head);
     }
    static void traverseExp(int depth, Absyn.FieldExpList e) {
       for(;e!=null;e=e.tail) traverseExp(depth,e.init);
    }
    static void traverseExp(int depth, Absyn.ArrayExp e) {
       traverseExp(depth,e.init);
       traverseExp(depth,e.size);
    }
    static void traverseExp(int depth, Absyn.AssignExp e) {
       traverseVar(depth,e.var);
       traverseExp(depth,e.exp);
    }
    static void traverseExp(int depth, Absyn.BreakExp e) { return; }
    static void traverseExp(int depth, Absyn.CallExp e) { traverseExp(depth,e.args); }
    static void traverseExp(int depth, Absyn.ForExp e) {
       escEnv.beginScope();
       traverseDec(depth,e.var);
       traverseExp(depth,e.hi);
       traverseExp(depth,e.body);
       escEnv.endScope();
    }
    static void traverseExp(int depth, Absyn.IfExp e) {
       traverseExp(depth,e.test);
       traverseExp(depth,e.thenclause);
       if(e.elseclause!=null) traverseExp(depth,e.elseclause);
    }
    static void traverseExp(int depth, Absyn.IntExp e) { return; }
    static void traverseExp(int depth, Absyn.LetExp e) {
       escEnv.beginScope();
       if(e.decs !=null) traverseDec(depth,e.decs);
       traverseExp(depth,e.body);
       escEnv.endScope();
    }
    static void traverseExp(int depth, Absyn.NilExp e) { return; }
    static void traverseExp(int depth, Absyn.OpExp e) {
       traverseExp(depth,e.left);
       traverseExp(depth,e.right);
    }
    static void traverseExp(int depth, Absyn.RecordExp e) {
       if(e.fields!=null) traverseExp(depth,e.fields);
    }
    static void traverseExp(int depth, Absyn.SeqExp e) {
       if(e.list!=null) traverseExp(depth,e.list);
    }
    static void traverseExp(int depth, Absyn.StringExp e) { return; }
    static void traverseExp(int depth, Absyn.VarExp e) { traverseVar(depth,e.var); }
    static void traverseExp(int depth, Absyn.WhileExp e) {
       traverseExp(depth,e.test);
       traverseExp(depth,e.body);
    }
    // DEC
    static void traverseDec(int depth, Absyn.DecList d) {
       for(;d!=null;d=d.tail) traverseDec(depth,d.head);
    }
    static void traverseDec(int depth, Absyn.FunctionDec d) {
       // per prendere anche la FunctionDec d.next
       for (;d!=null;d=d.next) {
          escEnv.beginScope();
          // inserisco i parametri nell'ambiente escEnv
          for(Absyn.FieldList p=d.params;p!=null;p=p.tail)  escEnv.put(p.name,new FormalEscape(depth+1,p));
          traverseExp(depth+1,d.body);
          escEnv.endScope();
       }
    }
    static void traverseDec(int depth, Absyn.VarDec d) {
       // inserisce la var nell'ambiente escEnv
       escEnv.put(d.name,new VarEscape(depth,d));
       traverseExp(depth,d.init);
    }
}