package AbstractSyntaxTree;

import Common.ErrorMsg;
import Common.Token;
import Symbol.Command;

public class ScanStatementAST extends AbstractSyntaxTree {
    private Token identifier;

    public ScanStatementAST(Token identifier) {
        this.identifier = identifier;
    }

    @Override
    public void generate() {
        String name = identifier.GetValueString();
        int localIndex = symbol.getVariableIndex(name, currentFunc);
        int globalIndex = symbol.getVariableIndex(name, "");
        if (localIndex == -1 && globalIndex == -1)
            ErrorMsg.Error("Undefined identifier");
        String funcName = localIndex != -1 ? currentFunc : "";
        if (symbol.isConst(name, funcName))
            ErrorMsg.Error("Assign to constant");
        int index = symbol.getVariableIndex(name, funcName);
        symbol.assignToVariable(name, funcName);
        symbol.addCommand(currentFunc, new Command("loada", localIndex != -1 ? 0 : 1, index));
        symbol.addCommand(currentFunc, new Command("iscan", -1, -1));
        symbol.addCommand(currentFunc, new Command("istore", -1, -1));
    }
}
