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
            switch (cmp_operator) {
                case LESS:    // a < b ----> a - b < 0
                    if_stmt_index = symbl.add_code(now_func, Order("jl", -1, -1));   //回填
                    break;
                case LE:      // a <= b ----> a - b <= 0
                    if_stmt_index = symbl.add_code(now_func, Order("jle", -1, -1));   //回填
                    break;
                case GREATER: // a > b ----> a - b > 0
                    if_stmt_index = symbl.add_code(now_func, Order("jg", -1, -1));   //回填
                    break;
                case GE:      // a >= b ----> a - b >= 0
                    if_stmt_index = symbl.add_code(now_func, Order("jge", -1, -1));   //回填
                    break;
                case EQUAL:   // a == b ----> a - b == 0
                    if_stmt_index = symbl.add_code(now_func, Order("je", -1, -1));   //回填
                    break;
                case NOT_EQUAL: // a != b ----> a - b != 0
                    if_stmt_index = symbl.add_code(now_func, Order("jne", -1, -1));   //回填
                    break;
                default:;
            }
        } else {
            if_stmt_index = symbl.add_code(now_func, Order("jg", -1, -1));      //回填
        }
        if (else_stmt) else_stmt->generate();
        else_stmt_end = symbl.add_code(now_func, Order("jmp", 0, -1));   //回填
        if_stmt->generate();
        if_stmt_end = symbl.get_order_last_num(now_func);
        symbl.fill_back(now_func, if_stmt_index, else_stmt_end + 1, -1);
        symbl.fill_back(now_func, else_stmt_end, if_stmt_end + 1, -1);
    }

}
