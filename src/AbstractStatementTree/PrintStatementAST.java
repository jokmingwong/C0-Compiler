package AbstractStatementTree;

import java.util.ArrayList;

public class PrintStatementAST extends AbstractSyntaxTree {
    private ArrayList<ExpressionAST>printExpr;
    public void _add(ExpressionAST p){
        printExpr.add(p);
    }
    public PrintStatementAST(){

    }

    @Override
    public void generate() {

    }
}
