package FindEscape;

public class FindEscape {
    Symbol.Table escEnv = new Symbol.Table(); // escEnv maps Symbol to Escape

    public FindEscape(Absyn.Exp e) {
        traverseExp(0, e);
    }

    void traverseVar(int depth, Absyn.Var v) {
        if (v == null) return;
        else if (v instanceof Absyn.SimpleVar) {
            Escape esc = (Escape)escEnv.get(((Absyn.SimpleVar)v).name);
            if (depth > esc.depth) esc.setEscape();
            System.out.println(depth);
        } else if (v instanceof Absyn.FieldVar) {
            traverseVar(depth + 1, ((Absyn.FieldVar)v).var);
            //Escape esc = (Escape)escEnv.get(((Absyn.FieldVar)v).field);
            //if (depth > esc.depth) esc.setEscape();
            System.out.println(depth);
        } else if (v instanceof Absyn.SubscriptVar) {
            traverseVar(depth + 1, ((Absyn.SubscriptVar)v).var);
            traverseExp(depth + 1, ((Absyn.SubscriptVar)v).index);
            System.out.println(depth);
        }
    }

    void traverseExp(int depth, Absyn.Exp e) {
        if (e == null) return;
        else if (e instanceof Absyn.VarExp) {
            traverseVar(depth + 1, ((Absyn.VarExp) e).var);
            System.out.println(depth);
        } else if (e instanceof Absyn.NilExp) return;
        else if (e instanceof Absyn.IntExp) return;
        else if (e instanceof Absyn.StringExp) return;
        else if (e instanceof Absyn.BreakExp) return;
        else if (e instanceof Absyn.CallExp) {
            Absyn.ExpList tmp;
            for (tmp = ((Absyn.CallExp)e).args; tmp != null; tmp = tmp.tail) {
                traverseExp(depth + 1, tmp.head);
                System.out.println(depth);
            }
        } else if (e instanceof Absyn.OpExp) {
            traverseExp(depth + 1, ((Absyn.OpExp)e).left);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.OpExp)e).right);
            System.out.println(depth);
        } else if (e instanceof Absyn.RecordExp) {
            Absyn.FieldExpList tmp;
            for (tmp = ((Absyn.RecordExp)e).fields; tmp != null; tmp = tmp.tail) {
                traverseExp(depth + 1, tmp.init);
                System.out.println(depth);
            }
        } else if (e instanceof Absyn.SeqExp) {
            Absyn.ExpList tmp;
            for (tmp = ((Absyn.SeqExp)e).list; tmp != null; tmp = tmp.tail) {
                traverseExp(depth + 1, tmp.head);
                System.out.println(depth);
            }
        } else if (e instanceof Absyn.AssignExp) {
            traverseVar(depth + 1, ((Absyn.AssignExp)e).var);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.AssignExp)e).exp);
            System.out.println(depth);
        } else if (e instanceof Absyn.IfExp) {
            traverseExp(depth + 1, ((Absyn.IfExp)e).test);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.IfExp)e).thenclause);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.IfExp)e).elseclause);
            System.out.println(depth);
        } else if (e instanceof Absyn.WhileExp) {
            traverseExp(depth + 1, ((Absyn.WhileExp)e).test);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.WhileExp)e).body);
            System.out.println(depth);
        } else if (e instanceof Absyn.ForExp) {
            traverseDec(depth + 1, ((Absyn.ForExp)e).var);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.ForExp)e).hi);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.ForExp)e).body);
            System.out.println(depth);
        } else if (e instanceof Absyn.LetExp) {
            traverseExp(depth + 1, ((Absyn.LetExp)e).body);
            System.out.println(depth);
            Absyn.DecList tmp;
            for (tmp = ((Absyn.LetExp)e).decs; tmp != null; tmp = tmp.tail) {
                traverseDec(depth + 1, tmp.head);
                System.out.println(depth);
            }
        } else if (e instanceof Absyn.ArrayExp) {
            traverseExp(depth + 1, ((Absyn.ArrayExp)e).size);
            System.out.println(depth);
            traverseExp(depth + 1, ((Absyn.ArrayExp)e).init);
            System.out.println(depth);
        }
    }

    void traverseDec(int depth, Absyn.Dec d) {
        if (d == null) return;
        else if (d instanceof Absyn.VarDec) {
            escEnv.put(((Absyn.VarDec)d).name, new VarEscape(depth, (Absyn.VarDec)d));
            System.out.println(depth);
        } else if (d instanceof Absyn.TypeDec) return;
        else if (d instanceof Absyn.FunctionDec) {
            escEnv.put(((Absyn.VarDec)d).name, new FormalEscape(depth, ((Absyn.FunctionDec)d).params));
            System.out.println(depth);
        }
    }
}