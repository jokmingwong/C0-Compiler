package AbstractSyntaxTree;

import Symbol.Symbol;

// Use AST to general code
public abstract class AbstractSyntaxTree {
    void generate(){}
    public static void print(Object o){
        System.out.print(o);
    }

    public static String currentFunc="";
    static int level=0;
    public static Symbol symbol=new Symbol();
    static String passParameters="";
}
