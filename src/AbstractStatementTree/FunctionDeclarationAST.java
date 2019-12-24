package AbstractStatementTree;

import Common.TokenType;

import java.util.ArrayList;

public class FunctionDeclarationAST extends AbstractSyntaxTree{
    private TokenType identifier;
    private ArrayList<VariableDeclarationAST>parameters;
    private ArrayList<CompoundStatementAST> compoundStatements;
    private TokenType returnType;
    public FunctionDeclarationAST(){}
    private void _add(VariableDeclarationAST p){
        parameters.add(p);
    }

    public void setIdentifier(TokenType identifier) {
        this.identifier = identifier;
    }
    public void setCompoundStatements(ArrayList<CompoundStatementAST> compoundStatements) {
        this.compoundStatements = compoundStatements;
    }

    public void setReturnType(TokenType returnType) {
        this.returnType = returnType;
    }

    public void generate(){

    }

    public void draw(){

    }
}
