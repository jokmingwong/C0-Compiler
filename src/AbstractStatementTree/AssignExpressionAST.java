package AbstractStatementTree;

import Common.Token;

public class AssignExpressionAST implements AbstractSyntaxTree {
    private Token identifier;
    private ExpressionAST value;

    public AssignExpressionAST(Token identifier, ExpressionAST value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public void generate() {

    }

}
