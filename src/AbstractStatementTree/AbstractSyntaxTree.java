package AbstractStatementTree;

import Symbol.Symbol;

public abstract class AbstractSyntaxTree {
    void generate(){}
    public static void print(Object o){
        System.out.print(o);
    }

    public static String currentFunc="";
    public static int level=0;
    public static Symbol symbol=new Symbol();
    public static String passParameters="";
}
