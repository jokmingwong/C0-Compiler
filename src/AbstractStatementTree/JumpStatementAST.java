package AbstractStatementTree;

import Common.Pair;

public class JumpStatementAST implements AbstractSyntaxTree {
    Pair<Integer,Integer>pos;
    ExpressionAST returnExpr;

    public JumpStatementAST(Pair<Integer, Integer> pos, ExpressionAST returnExpr) {
        this.pos = pos;
        this.returnExpr = returnExpr;
    }

    @Override
    public void generate() {

    }

    @Override
    public void draw() {

    }
}
