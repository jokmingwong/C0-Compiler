package AbstractStatementTree;

import Common.ErrorMsg;
import Common.Token;
import Common.TokenType;
import Symbol.Command;

public class PrimaryExpressionAST extends AbstractSyntaxTree{
    private enum PEType {INIT,IDENTIFIER,INTEGER,FUNC_CALL,EXPR}
    private PEType state =PEType.INIT;
    private Token base;
    private FunctionCallAST functionCalls;
    private ExpressionAST expr;
    public PrimaryExpressionAST(Token t){
        base=t;
        if(t.GetType()!= TokenType.INTEGER)state=PEType.IDENTIFIER;
        else state=PEType.INTEGER;
    }

    public PrimaryExpressionAST(FunctionCallAST fc){
        functionCalls=fc;
        state=PEType.FUNC_CALL;
    }

    public PrimaryExpressionAST(ExpressionAST ex){
        this.expr =ex;
        state=PEType.EXPR;
    }

    @Override
    public void generate() {
        switch (state) {
            case IDENTIFIER: {
                int localIndex = symbol.getVariableIndex(base.GetValueString(), currentFunc);
                int globalIndex = symbol.getVariableIndex(base.GetValueString(), "");
                if (localIndex == -1 && globalIndex == -1)
                    ErrorMsg.Error("Undefined identifier");
                if (localIndex != -1) {
                    Boolean isInit = symbol.isInitialized(base.GetValueString(), currentFunc);
                    if (!isInit)
                        ErrorMsg.Error("Undefined identifier");
                    symbol.addCommand(currentFunc, new Command("loada", 0, localIndex));
                    symbol.addCommand(currentFunc, new Command("iload", -1, -1));
                } else {
                    boolean is_init = symbol.isInitialized(base.GetValueString(), "");
                    if (!is_init)
                        ErrorMsg.Error("Undefined identifier");
                    symbol.addCommand(currentFunc, new Command("loada", 1, globalIndex));
                    symbol.addCommand(currentFunc, new Command("iload", -1, -1));
                }
                break;
            }
            case INTEGER: {
                symbol.addCommand(currentFunc, new Command("ipush", Integer.parseInt(base.GetValueString()),-1));
                break;
            }
            case FUNC_CALL: {
                String name = functionCalls.getIdentifier().GetValueString();
                int index = symbol.getFunctionIndex(name);
                if (index == -1)
                    ErrorMsg.Error("Undefined identifier");

                boolean isVoid = symbol.isVoid(name);
                if (isVoid)
                    ErrorMsg.Error("Undefined identifier");

                functionCalls.generate();
                break;
            }
            case EXPR: {
                expr.generate();
                break;
            }
            default:;
        }
    }

}
