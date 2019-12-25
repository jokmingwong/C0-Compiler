package AbstractStatementTree;

import Common.TokenType;

public class LoopStatementAST implements AbstractSyntaxTree {
    private ExpressionAST left;
    private TokenType compareOperator;
    private ExpressionAST right;
    private StatementAST loopStatement;

    public LoopStatementAST(ExpressionAST left, TokenType compareOperator, ExpressionAST right, StatementAST loopStatement) {
        this.left = left;
        this.compareOperator = compareOperator;
        this.right = right;
        this.loopStatement = loopStatement;
    }

    @Override
    public void generate() {

    }

    @Override
    public void draw() {

    }
}
