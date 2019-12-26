package AbstractStatementTree;

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
        int loop_stmt_index = -1, loop_stmt_end = -1;
        int first = symbol.getCommandLastNum(currentFunc);
        left.generate();
        if (right!=null) {
            right.generate();
            symbol.addCommand(currentFunc, new Command("isub", -1, -1));
            switch (compareOperator) {         //第一句判断与原condition相反
                case LESS:    // a < b ----> a - b < 0
                    loop_stmt_index = symbol.addCommand(currentFunc, new Command("jge", -1, -1));   //回填
                    break;
                case LE:      // a <= b ----> a - b <= 0
                    loop_stmt_index = symbol.addCommand(currentFunc, new Command("jg", -1, -1));   //回填
                    break;
                case GREATER: // a > b ----> a - b > 0
                    loop_stmt_index = symbol.addCommand(currentFunc, new Command("jle", -1, -1));   //回填
                    break;
                case GE:      // a >= b ----> a - b >= 0
                    loop_stmt_index = symbol.addCommand(currentFunc, new Command("jl", -1, -1));   //回填
                    break;
                case EQUAL:   // a == b ----> a - b == 0
                    loop_stmt_index = symbol.addCommand(currentFunc, new Command("jne", -1, -1));   //回填
                    break;
                case NOT_EQUAL: // a != b ----> a - b != 0
                    loop_stmt_index = symbol.addCommand(currentFunc, new Command("je", -1, -1));   //回填
                    break;
                default:;
            }
        } else {
            loop_stmt_index = symbol.addCommand(currentFunc, new Command("jle", -1, -1));      //回填
        }
        loopStatement.generate();
        loop_stmt_end = symbol.addCommand(currentFunc, new Command("jmp", first + 1, -1));
        symbol.fillback(currentFunc, loop_stmt_index, loop_stmt_end + 1, -1);
    }

}
