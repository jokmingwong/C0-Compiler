package AbstractSyntaxTree;

import Common.ErrorMsg;
import Common.Token;
import Symbol.Command;

import java.util.ArrayList;

public class FunctionCallAST extends AbstractSyntaxTree {
    private Token identifier;
    private ArrayList<ExpressionAST> exprList;

    public FunctionCallAST(Token t){
        exprList=new ArrayList<>();
        identifier=t;
        exprList.clear();
    }

    public void add(ExpressionAST p){
        exprList.add(p);
    }

    Token getIdentifier(){
        return identifier;
    }

    @Override
    public void generate() {
        int index = symbol.getFunctionIndex(identifier.GetValueString());
        if (index == -1)
            ErrorMsg.Error("Undefined function");
        int varIndexLocal = symbol.getVariableIndex(identifier.GetValueString(), currentFunc);
        if (varIndexLocal != -1)
            ErrorMsg.Error("Error function mask");

        for (ExpressionAST e : exprList) e.generate();
        symbol.addCommand(currentFunc, new Command("call", index, -1));
    }
}
