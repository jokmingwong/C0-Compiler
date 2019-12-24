package AbstractStatementTree;

import Common.Token;

public class AssignExpressionAST extends AbstractSyntaxTree {
    private Token identifier;
    ExpressionAST value;
}
