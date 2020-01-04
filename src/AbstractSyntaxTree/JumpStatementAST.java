package AbstractSyntaxTree;

import Common.ErrorMsg;
import Common.Pair;
import Symbol.Command;

public class JumpStatementAST extends AbstractSyntaxTree {
    private Pair<Integer, Integer> pos;
    private ExpressionAST returnExpr;

    public JumpStatementAST(Pair<Integer, Integer> pos, ExpressionAST returnExpr) {
        this.pos = pos;
        this.returnExpr = returnExpr;
    }

    @Override
    public void generate() {
        if (symbol.isVoid(currentFunc) && returnExpr != null)
            ErrorMsg.Error("Void function with value");
        if (!symbol.isVoid(currentFunc) && returnExpr == null)
            ErrorMsg.Error("Void function with value");

        if (returnExpr != null) {
            returnExpr.generate();
            symbol.addCommand(currentFunc, new Command("iret", -1, -1));
        } else {
            symbol.addCommand(currentFunc, new Command("ret", -1, -1));
        }
    }
}

