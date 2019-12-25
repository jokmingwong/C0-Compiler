package AbstractStatementTree;

import Common.ErrorMsg;
import Common.Token;
import Symbol.Command;

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

    @Override
    public void generate(){
        int index=symbol.getVariableIndex(identifier.GetValueString(),currentFunc);
        if(index!=-1)
            ErrorMsg.Error("Duplicate declare");
        int funcIndex=symbol.getFunctionIndex(identifier.GetValueString());
        if(funcIndex !=-1 && currentFunc.equals(""))
            ErrorMsg.Error("Duplicate declare");
        symbol.addVariable(identifier.GetValueString(),isConst,isInit,currentFunc);
        if(passParameters.equals("passing")){
            if(!isInit && isConst)
                ErrorMsg.Error("Constant need value");
            else if(!isInit)
                symbol.addCommand(currentFunc,new Command("snew",1,-1));
            else
                value.generate();
        }
    }

}
