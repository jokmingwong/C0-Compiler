package AbstractStatementTree;

import java.util.ArrayList;

public class StatementAST extends AbstractSyntaxTree{
    private ArrayList<StatementAST> statements;
    private AbstractSyntaxTree singleStatement;
    public StatementAST(){
        singleStatement =null;
    }

    public void _add(StatementAST p){
        statements.add(p);
    }
    public void setSingleStatement(AbstractSyntaxTree p){
        singleStatement =p;
    }

    @Override
    public void generate() {
        if (singleStatement!=null) singleStatement.generate();
        else {
            for (StatementAST s : statements) if (s!=null) s.generate();
        }

    }

}
