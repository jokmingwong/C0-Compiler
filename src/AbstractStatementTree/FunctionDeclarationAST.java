package AbstractStatementTree;

import Common.ErrorMsg;
import Common.Token;
import Common.TokenType;

import java.util.ArrayList;

public class FunctionDeclarationAST extends AbstractSyntaxTree{
    private Token identifier;
    private ArrayList<VariableDeclarationAST>parameters;
    private CompoundStatementAST compoundStatements;
    private TokenType returnType;
    public FunctionDeclarationAST(){
        parameters=new ArrayList<>();
    }
    public void add(VariableDeclarationAST p){
        parameters.add(p);
    }

    public void setIdentifier(Token identifier) {
        this.identifier = identifier;
    }
    public void setCompoundStatements(CompoundStatementAST compoundStatements) {
        this.compoundStatements = compoundStatements;
    }

    public void setReturnType(TokenType returnType) {
        this.returnType = returnType;
    }

    public void generate(){
        String name=identifier.GetValueString();
        int funcIndex=symbol.getFunctionIndex(name);
        int varIndex=symbol.getVariableIndex(name,"");
        if(funcIndex != -1 || varIndex != -1)
            ErrorMsg.Error("Error duplicate");
        symbol.addFunction(name,parameters.size(),returnType);
        currentFunc=name;
        level=1;
        symbol.localIndex=0;
        passParameters = "passing";
        for (VariableDeclarationAST v : parameters)
            v.generate();
        passParameters = "";
        compoundStatements.generate();
    }

}
