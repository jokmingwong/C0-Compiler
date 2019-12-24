package AbstractStatementTree;

import Common.Token;

public class VariableDeclarationAST extends AbstractSyntaxTree {
    private boolean isConst;
    private boolean isInit;
    private Token identifier;
    private ExpressionAST value;

    public VariableDeclarationAST(boolean isConst, boolean isInit, Token identifier, ExpressionAST value) {
        this.isConst = isConst;
        this.isInit = isInit;
        this.identifier = identifier;
        this.value = value;
    }

    public VariableDeclarationAST(boolean isConst,Token identifier) {
        this.isConst = isConst;
        this.isInit=true;
        this.identifier = identifier;
    }

    void generate(){

    }

    void draw(){

    }
}
