package Mips;
import java.util.Hashtable;
import Symbol.Symbol;
import Temp.Temp;
import Temp.TempList;
import Temp.Label;
import Frame.Frame;
import Frame.Access;
import Frame.AccessList;
import Util.BoolList;

public class MipsFrame extends Frame {

    private int count = 0;
    private int offset = 0;
    public Frame newFrame(Symbol name, Util.BoolList formals) {
        Label label;
        if (name == null)
            label = new Label();
        else if (this.name != null)
            label = new Label(this.name + "." + name + "." + count++);
        else
            label = new Label(name);
        return new MipsFrame(label, formals);
    }

    public MipsFrame() {}
    public MipsFrame(Label n, Util.BoolList f) {
        name = n;
        formals = allocFormals(0, f);
    }

    private static final int wordSize = 4;
    public int wordSize() {
        return wordSize;
    }

    public Access allocLocal(boolean escape) {
        if (escape) {
            offset -= wordSize;
            return new InFrame(offset);
        } else
            return new InReg(new Temp());
    }

    private AccessList allocFormals(int offset, Util.BoolList formals) {
        if (formals == null)
            return null;
        Access a;
        if (formals.head)
            a = new InFrame(offset);
        else
            a = new InReg(new Temp());
        return new AccessList(a, allocFormals(offset + wordSize, formals.tail));
    }

    static final Temp ZERO = new Temp(); // zero reg

    static final Temp AT = new Temp(); // reserved for assembler

    static final Temp V0 = new Temp(); // function result

    static final Temp V1 = new Temp(); // second function result

    static final Temp A0 = new Temp(); // argument1

    static final Temp A1 = new Temp(); // argument2

    static final Temp A2 = new Temp(); // argument3

    static final Temp A3 = new Temp(); // argument4

    static final Temp T0 = new Temp(); // caller-saved

    static final Temp T1 = new Temp();

    static final Temp T2 = new Temp();

    static final Temp T3 = new Temp();

    static final Temp T4 = new Temp();

    static final Temp T5 = new Temp();

    static final Temp T6 = new Temp();

    static final Temp T7 = new Temp();

    static final Temp S0 = new Temp(); // callee-saved

    static final Temp S1 = new Temp();

    static final Temp S2 = new Temp();

    static final Temp S3 = new Temp();

    static final Temp S4 = new Temp();

    static final Temp S5 = new Temp();

    static final Temp S6 = new Temp();

    static final Temp S7 = new Temp();

    static final Temp T8 = new Temp(); // caller-saved

    static final Temp T9 = new Temp();

    static final Temp K0 = new Temp(); // reserved for OS kernel

    static final Temp K1 = new Temp(); // reserved for OS kernel

    static final Temp GP = new Temp(); // pointer to global area

    static final Temp SP = new Temp(); // stack pointer

    static final Temp FP = new Temp(); // virtual frame pointer (eliminated)

    static final Temp S8 = new Temp(); // actual frame pointer

    static final Temp RA = new Temp(); // return address

    public Temp FP() {
        return FP;
    }

    public Temp RV() {
        return V0;
    }

    private static Hashtable labels = new Hashtable();

    public String string(Label lab, String string) {
        int length = string.length();
        String lit = "";
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            switch (c) {
            case '\b':
                lit += "\\b";
                break;
            case '\t':
                lit += "\\t";
                break;
            case '\n':
                lit += "\\n";
                break;
            case '\f':
                lit += "\\f";
                break;
            case '\r':
                lit += "\\r";
                break;
            case '\"':
                lit += "\\\"";
                break;
            case '\\':
                lit += "\\\\";
                break;
            default:
                if (c < ' ' || c > '~') {
                    int v = (int) c;
                    lit += "\\" + ((v >> 6) & 7) + ((v >> 3) & 7) + (v & 7);
                } else
                    lit += c;
                break;
            }
        }
        return ".data\n" + lab.toString() + ":.word " + length + "\n"
                + "\t.asciiz\t\"" + lit + "\"";
    }

    private static final Label badPtr = new Label("_BADPTR");

    public Label badPtr() {
        return badPtr;
    }

    private static final Label badSub = new Label("_BADSUB");

    public Label badSub() {
        return badSub;
    }

    private static final Hashtable tempMap = new Hashtable(32);
    static {
        tempMap.put(ZERO, "$0");
        tempMap.put(AT, "$at");
        tempMap.put(V0, "$v0");
        tempMap.put(V1, "$v1");
        tempMap.put(A0, "$a0");
        tempMap.put(A1, "$a1");
        tempMap.put(A2, "$a2");
        tempMap.put(A3, "$a3");
        tempMap.put(T0, "$t0");
        tempMap.put(T1, "$t1");
        tempMap.put(T2, "$t2");
        tempMap.put(T3, "$t3");
        tempMap.put(T4, "$t4");
        tempMap.put(T5, "$t5");
        tempMap.put(T6, "$t6");
        tempMap.put(T7, "$t7");
        tempMap.put(S0, "$s0");
        tempMap.put(S1, "$s1");
        tempMap.put(S2, "$s2");
        tempMap.put(S3, "$s3");
        tempMap.put(S4, "$s4");
        tempMap.put(S5, "$s5");
        tempMap.put(S6, "$s6");
        tempMap.put(S7, "$s7");
        tempMap.put(T8, "$t8");
        tempMap.put(T9, "$t9");
        tempMap.put(K0, "$k0");
        tempMap.put(K1, "$k1");
        tempMap.put(GP, "$gp");
        tempMap.put(SP, "$sp");
        tempMap.put(FP, "$fp"); // should be virtual
        tempMap.put(S8, "$s8"); // $s8 is alias for $fp (when virtual)
        tempMap.put(RA, "$ra");
    }

    public String tempMap(Temp temp) {
        if (temp == null) {
            System.out.print("haha");
        }
        if (tempMap.containsKey(temp)) {
            return (String) tempMap.get(temp);
        } else {
            return temp.toString();
        }
    }

    static TempList L(Temp h, TempList t) {
        return new TempList(h, t);
    }

    static TempList L(Temp h) {
        return new TempList(h, null);
    }

    static TempList L(TempList a, TempList b) {
        return new TempList(a, b);
    }

    static final int callerSavesOffset = 0;

    static final int calleeSavesOffset = 0;

    // Register lists: must not overlap and must include every register that
    // might show up in Assem instructions
    static TempList specialRegs, argRegs, tempSaves, callerSaves, calleeSaves;
    {
        // registers dedicated to special purposes
        specialRegs = L(ZERO, L(AT, L(K0, L(K1, L(GP, L(FP, L(SP, L(RA))))))));
        // registers in which to pass outgoing arguments (including static link)
        argRegs = L(A0, L(A1, L(A2, L(A3))));
        // registers that the called procedure (callee) must preserve for caller
        calleeSaves = L(S0, L(S1, L(S2,
                L(S3, L(S4, L(S5, L(S6, L(S7, L(S8)))))))));
        // registers that the callee may trash
        tempSaves = L(T0, L(T1, L(T2, L(T3, L(T4, L(T5, L(T6, L(T7,
                L(T8, L(T9))))))))));
        callerSaves = L(V0, L(V1, tempSaves));
    }

    static TempList append(TempList a, TempList b) {
        return new TempList(a, b);
    }

    static TempList calldefs, returnSink;
    {
        // registers defined by a call
        calldefs = L(RA, L(argRegs, callerSaves));
        // registers live on return
        returnSink = L(V0, L(specialRegs, calleeSaves));
    }

    int maxArgs = 0;

    public String pre() {
        int frameSize = maxArgs * wordSize - offset + 80;
        int i;

        String pr = ".text\n" + name + ":\n" + name + "_framesize=" + frameSize;
        if (frameSize != 0) {
            pr += "\nsub $sp," + name + "_framesize";
        }
        TempList callee = calleeSaves;
        int offs = offset;
        offs -= wordSize;
        pr = pr + "\nsw " + "$ra" + "," + offs + "+" + name + "_framesize($sp)";
        while (callee != null) {
            offs -= wordSize;
            pr = pr + "\nsw " + tempMap(callee.head) + "," + offs + "+" + name
                    + "_framesize($sp)";
            callee = callee.tail;
        }
        pr = pr + "\n";
        return pr;
    }

    public String post() {
        int frameSize = maxArgs * wordSize - offset + 80;
        String po = "";
        TempList callee = calleeSaves;
        int offs = offset;
        offs -= wordSize;
        po = po + "\nlw " + "$ra" + "," + offs + "+" + name + "_framesize($sp)";
        while (callee != null) {
            offs -= wordSize;
            po = po + "\nlw " + tempMap(callee.head) + "," + offs + "+" + name
                    + "_framesize($sp)";
            callee = callee.tail;
        }
        if (frameSize != 0) {
            po = po + "\naddu $sp," + name + "_framesize\n";
        }
        po = po + "\njr $ra\n";
        return po;
    }

    public Temp SP() {
        // TODO Auto-generated method stub
        return SP;
    }

    // Return an array of registers available for register allocation
    public TempList colors() {
        TempList colors = null;
        // colors=append(colors,callerSaves);
        colors = append(colors, tempSaves);
        // colors=append(colors,argRegs);
        return colors;
    }

    public Frame newFrame(Label name, BoolList formals) {
        // TODO Auto-generated method stub
        return null;
    }

    public TempList registers() {
        TempList registers = null;
        registers = append(registers, callerSaves);
        registers = append(registers, calleeSaves);
        registers = append(registers, argRegs);
        registers = append(registers, specialRegs);
        return registers;
    }
}