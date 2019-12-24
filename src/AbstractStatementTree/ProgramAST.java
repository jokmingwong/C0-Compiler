package AbstractStatementTree;

import Symbol.Symbol;

import java.util.ArrayList;

// TODO: recode
public class ProgramAST extends AbstractSyntaxTree {
    private ArrayList<VariableDeclarationAST>vars;
    private ArrayList<FunctionDeclarationAST>funcs;

    public ProgramAST(){}

    void generate() {}
    void draw(){}

    Symbol _generate(){

    }

    private void _add(VariableDeclarationAST p){
        vars.add(p);
    }

    private void _add(FunctionDeclarationAST p){
        funcs.add(p);
    }

}
