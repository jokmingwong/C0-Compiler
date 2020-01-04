package AbstractSyntaxTree;

import Common.TokenType;
import Symbol.Command;

public class UnaryExpressionAST extends AbstractSyntaxTree{
    private PrimaryExpressionAST priExpr;
    private TokenType unaryOperator;

    public UnaryExpressionAST(TokenType unaryOperator,PrimaryExpressionAST priExpr) {
        this.priExpr = priExpr;
        this.unaryOperator = unaryOperator;
    }

    @Override
    public void generate(){
        priExpr.generate();
        if (unaryOperator == TokenType.MINUS)
            symbol.addCommand(currentFunc, new Command("ineg", -1, -1));
    }
}
