package AbstractSyntaxTree;

import Common.TokenType;
import Symbol.Command;

public class LoopStatementAST extends AbstractSyntaxTree {
    private ExpressionAST left;
    private TokenType compareOperator;
    private ExpressionAST right;
    private StatementAST loopStatement;

    public LoopStatementAST(ExpressionAST left, TokenType compareOperator, ExpressionAST right, StatementAST loopStatement) {
        this.left = left;
        this.compareOperator = compareOperator;
        this.right = right;
        this.loopStatement = loopStatement;
    }

    @Override
    public void generate() {
        int loopStatementIndex = -1, loopStatementEnd = -1;
        int first = symbol.getCommandLastNum(currentFunc);
        left.generate();
        if (right!=null) {
            right.generate();
            symbol.addCommand(currentFunc, new Command("isub", -1, -1));
            switch (compareOperator) {
                // Fill back the command
                case LESS:
                    loopStatementIndex = symbol.addCommand(currentFunc, new Command("jge", -1, -1));
                    break;
                case LE:
                    loopStatementIndex = symbol.addCommand(currentFunc, new Command("jg", -1, -1));
                    break;
                case GREATER:
                    loopStatementIndex = symbol.addCommand(currentFunc, new Command("jle", -1, -1));
                    break;
                case GE:
                    loopStatementIndex = symbol.addCommand(currentFunc, new Command("jl", -1, -1));
                    break;
                case EQUAL:
                    loopStatementIndex = symbol.addCommand(currentFunc, new Command("jne", -1, -1));
                    break;
                case NOT_EQUAL:
                    loopStatementIndex = symbol.addCommand(currentFunc, new Command("je", -1, -1));
                    break;
                default:
                    break;
            }
        } else {
            loopStatementIndex = symbol.addCommand(currentFunc, new Command("jle", -1, -1));
        }
        loopStatement.generate();
        loopStatementEnd = symbol.addCommand(currentFunc, new Command("jmp", first + 1, -1));
        symbol.fillBack(currentFunc, loopStatementIndex, loopStatementEnd + 1, -1);
    }

}
