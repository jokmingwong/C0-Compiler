package AbstractStatementTree;

import Symbol.Symbol;

import java.util.ArrayList;


public class ProgramAST implements AbstractSyntaxTree {
    private ArrayList<VariableDeclarationAST>vars;
    private ArrayList<FunctionDeclarationAST>funcs;

    public ProgramAST(){}

    public void generate() {}


    Symbol _generate(){
        for(VariableDeclarationAST v:vars)
            v.generate();
        for(FunctionDeclarationAST f:funcs)
            f.generate();
        return symbol;
    }

    private void _add(VariableDeclarationAST p){
        vars.add(p);
    }

    private void _add(FunctionDeclarationAST p){
        funcs.add(p);
    }

}
