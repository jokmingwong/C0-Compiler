package AbstractStatementTree;

import Symbol.Symbol;

import java.util.ArrayList;


public class ProgramAST extends AbstractSyntaxTree {
    private ArrayList<VariableDeclarationAST>vars;
    private ArrayList<FunctionDeclarationAST>funcs;

    public ProgramAST(){
        vars=new ArrayList<>();
        funcs=new ArrayList<>();
    }

    public void generate() {}


    public Symbol _generate(){
        for(VariableDeclarationAST v:vars)
            v.generate();
        for(FunctionDeclarationAST f:funcs)
            f.generate();
        return symbol;
    }

    public void _add(VariableDeclarationAST p){
        vars.add(p);
    }

    public void _add(FunctionDeclarationAST p){
        funcs.add(p);
    }

}
