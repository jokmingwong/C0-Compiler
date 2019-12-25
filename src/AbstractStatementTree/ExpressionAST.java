package AbstractStatementTree;

import Common.TokenType;
import Symbol.Command;

import java.util.ArrayList;

public class ExpressionAST extends AbstractSyntaxTree {
    private ArrayList<MultiplicativeExpressionAST> multiExpr;
    private ArrayList<TokenType> operation;

    private void _add(MultiplicativeExpressionAST p) {
        multiExpr.add(p);
    }

    private void _add(TokenType t) {
        operation.add(t);
    }

    public ExpressionAST() {
    }

    @Override
    public void generate() {
        if (multiExpr.size() == 1) multiExpr.get(0).generate();
        else {
            int index = 2;
            multiExpr.get(0).generate();
            multiExpr.get(1).generate();
            symbol.addCommand(currentFunc,
                    new Command(operation.get(0) == TokenType.PLUS ? "iadd" : "isub", -1, -1));
            while (index < multiExpr.size()) {
                multiExpr.get(index).generate();
                symbol.addCommand(currentFunc,
                        new Command(operation.get(index - 1) == TokenType.PLUS ? "iadd" : "isub", -1, -1));
                index++;
            }
        }
    }


}
