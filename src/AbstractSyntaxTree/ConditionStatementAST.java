package AbstractSyntaxTree;

import Common.TokenType;
import Symbol.Command;

public class ConditionStatementAST extends AbstractSyntaxTree {
    private ExpressionAST left;
    private TokenType compareOperator;
    private ExpressionAST right;
    private StatementAST ifStatement;
    private StatementAST elseStatement;

    public ConditionStatementAST(ExpressionAST left, TokenType compareOperator, ExpressionAST right, StatementAST ifStatement, StatementAST elseStmt) {
        this.left = left;
        this.compareOperator = compareOperator;
        this.right = right;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStmt;
    }

    @Override
    public void generate() {
        int ifStatementIndex = -1, ifStatementEnd, elseStatementEnd ;
        left.generate();
        if (right!=null) {
            right.generate();
            symbol.addCommand(currentFunc, new Command("isub", -1, -1));
            // Change comparision between variables comparision to zero
            switch (compareOperator) {
                // If in it, fill back the command
                case LESS:
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jl", -1, -1));
                    break;
                case LE:
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jle", -1, -1));
                    break;
                case GREATER:
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jg", -1, -1));
                    break;
                case GE:
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jge", -1, -1));
                    break;
                case EQUAL:
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("je", -1, -1));
                    break;
                case NOT_EQUAL:
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jne", -1, -1));
                    break;
                default:
                    break;
            }
        } else {
            ifStatementIndex = symbol.addCommand(currentFunc, new Command("jg", -1, -1));
        }
        if (elseStatement!=null) elseStatement.generate();
        elseStatementEnd = symbol.addCommand(currentFunc, new Command("jmp", 0, -1));
        ifStatement.generate();
        ifStatementEnd = symbol.getCommandLastNum(currentFunc);
        symbol.fillBack(currentFunc, ifStatementIndex, elseStatementEnd + 1, -1);
        symbol.fillBack(currentFunc, elseStatementEnd, ifStatementEnd + 1, -1);
    }

}
