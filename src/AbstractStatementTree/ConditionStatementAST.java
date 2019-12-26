package AbstractStatementTree;

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
        int ifStatementIndex = -1, ifStatementEnd = -1, elseStatementEnd = -1;
        left.generate();
        if (right!=null) {
            right.generate();
            symbol.addCommand(currentFunc, new Command("isub", -1, -1));
            switch (compareOperator) {
                case LESS:    // a < b ----> a - b < 0
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jl", -1, -1));   //回填
                    break;
                case LE:      // a <= b ----> a - b <= 0
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jle", -1, -1));   //回填
                    break;
                case GREATER: // a > b ----> a - b > 0
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jg", -1, -1));   //回填
                    break;
                case GE:      // a >= b ----> a - b >= 0
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jge", -1, -1));   //回填
                    break;
                case EQUAL:   // a == b ----> a - b == 0
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("je", -1, -1));   //回填
                    break;
                case NOT_EQUAL: // a != b ----> a - b != 0
                    ifStatementIndex = symbol.addCommand(currentFunc, new Command("jne", -1, -1));   //回填
                    break;
                default:
                    break;
            }
        } else {
            ifStatementIndex = symbol.addCommand(currentFunc, new Command("jg", -1, -1));      //回填
        }
        if (elseStatement!=null) elseStatement.generate();
        elseStatementEnd = symbol.addCommand(currentFunc, new Command("jmp", 0, -1));   //回填
        ifStatement.generate();
        ifStatementEnd = symbol.getCommandLastNum(currentFunc);
        symbol.fillback(currentFunc, ifStatementIndex, elseStatementEnd + 1, -1);
        symbol.fillback(currentFunc, elseStatementEnd, ifStatementEnd + 1, -1);
    }

}
