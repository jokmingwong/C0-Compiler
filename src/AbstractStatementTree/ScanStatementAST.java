package AbstractStatementTree;

import Common.Token;

public class ScanStatementAST implements AbstractSyntaxTree {
    private Token identifier;

    public ScanStatementAST(Token identifier) {
        this.identifier = identifier;
    }

    @Override
    public void generate() {

    }

    @Override
    public void draw() {

    }
}
