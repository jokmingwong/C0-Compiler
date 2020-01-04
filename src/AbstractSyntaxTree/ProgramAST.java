package AbstractSyntaxTree;

import Symbol.Symbol;

import java.util.ArrayList;


public class ProgramAST extends AbstractSyntaxTree {
    private ArrayList<VariableDeclarationAST>vars;
    private ArrayList<FunctionDeclarationAST> func;

    public ProgramAST(){
        vars=new ArrayList<>();
        func =new ArrayList<>();
    }

    public void generate() {}


    public Symbol _generate(){
        for(VariableDeclarationAST v:vars)
            v.generate();
        for(FunctionDeclarationAST f: func)
            f.generate();
        return symbol;
    }

    public void add(VariableDeclarationAST p){
        vars.add(p);
    }

    public void add(FunctionDeclarationAST p){
        func.add(p);
    }

}
