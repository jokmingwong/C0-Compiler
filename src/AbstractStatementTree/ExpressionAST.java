package AbstractStatementTree;

import Common.TokenType;

import java.util.ArrayList;

public class ExpressionAST extends AbstractSyntaxTree {
    private ArrayList<MultiplicativeExpressionAST> multiExpr;
    private ArrayList<TokenType> operation;
    private void _add(MultiplicativeExpressionAST p){
        multiExpr.add(p);
    }

    private void _add(TokenType t){
        operation.add(t);
    }

    public ExpressionAST() {}

    public void generate(){}

    public void draw(){}

}
