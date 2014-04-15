package Translate;

public abstract class Exp {
  abstract Tree.Exp unEx();
  abstract Tree.Stm unNx();
  abstract Tree.Stm unCx(Temp.Label t, Temp.Label f);
}
