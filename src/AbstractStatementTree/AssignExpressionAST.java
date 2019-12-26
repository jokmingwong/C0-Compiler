package AbstractStatementTree;

import Common.ErrorMsg;
import Common.Token;
import Symbol.Command;

public class AssignExpressionAST extends AbstractSyntaxTree {
    private Token identifier;
    private ExpressionAST value;

    public AssignExpressionAST(Token identifier, ExpressionAST value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public void generate() {
        String name = identifier.GetValueString();
        int localIndex = symbol.getVariableIndex(name, currentFunc);
        int global_index = symbol.getVariableIndex(name, "");
        if (localIndex == -1 && global_index == -1)
            ErrorMsg.Error("Using undefined identifier");
        String funcName = localIndex != -1 ? currentFunc : "";
        if (symbol.isConst(name, funcName))
            ErrorMsg.Error("Assign to constant");
        int index = symbol.getVariableIndex(name, funcName);
        symbol.addCommand(currentFunc, new Command("loada", localIndex != -1 ? 0 : 1, index));
        value.generate();
        symbol.addCommand(currentFunc, new Command("istore", -1, -1));
        symbol.assignToVariable(name, funcName);
    }

}
