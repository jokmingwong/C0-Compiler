package AbstractSyntaxTree;

import Symbol.Command;

import java.util.ArrayList;

public class CompoundStatementAST extends AbstractSyntaxTree{
    private ArrayList<VariableDeclarationAST>vars;
    private ArrayList<StatementAST> stmt;
    public CompoundStatementAST(){
        stmt=new ArrayList<>();
        vars=new ArrayList<>();
    }

    public void add(VariableDeclarationAST p){
        vars.add(p);
    }

    public void add(StatementAST p){
        stmt.add(p);
    }

    @Override
    public void generate() {
        for (VariableDeclarationAST v : vars) v.generate();
        for (StatementAST s : stmt) if (s!=null) s.generate();
        if(symbol.isVoid(currentFunc))
            symbol.addCommand(currentFunc,new Command("ret",-1,-1));
        else{
            symbol.addCommand(currentFunc,new Command("ipush",0,-1));
            symbol.addCommand(currentFunc,new Command("iret",-1,-1));
        }
    }
}
