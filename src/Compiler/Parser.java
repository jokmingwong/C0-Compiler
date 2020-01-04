package Compiler;

import AbstractSyntaxTree.*;
import Common.ErrorMsg;
import Common.Pair;
import Common.Token;
import Common.TokenType;

import java.util.ArrayList;

import static Common.TokenType.NUL;
import static Common.TokenType.RIGHT_BRACE;

/**
 * Change from miniplc0
 */

public class Parser {
    private ArrayList<Token> tokens;
    private int offset;
    private Pair<Integer, Integer> currentPos;
    private int _nextTokenIndex;

    public Parser(ArrayList<Token> v) {
        tokens = v;
        offset = 0;
        currentPos = new Pair<>(0, 0);
        _nextTokenIndex = 0;
    }

    private Token nextToken() {
        if (offset == tokens.size())
            return new Token();
        currentPos = tokens.get(offset).GetEndPos();
        return tokens.get(offset++);
    }

    private void unreadToken() {
        if (offset == 0)
            return;
        currentPos = tokens.get(offset - 1).GetEndPos();
        offset--;
    }

    // Interface
    public ProgramAST parse() {
        return parseProgram();
    }

    private ProgramAST parseProgram() {
        ProgramAST pro = new ProgramAST();
        // Variable declaration
        while (true) {
            // ( TOKEN )
            Token first = nextToken();
            nextToken();
            Token third = nextToken();
            unreadToken();
            unreadToken();
            unreadToken();
            if (first.isEnd()) return pro;

            // If the third token is '(', assuming it is left parent
            if (third.GetType() == TokenType.LEFT_PARENT)
                break;

            ArrayList<VariableDeclarationAST> p = parseVariableDeclaration();
            for (VariableDeclarationAST v : p) pro.add(v);
        }
        // Function declaration
        while (true) {
            Token first = nextToken();
            unreadToken();
            if (first.isEnd()) return pro;
            pro.add(parseFunctionDefinition());
        }
    }

    //变量声明
    private ArrayList<VariableDeclarationAST> parseVariableDeclaration() {
        ArrayList<VariableDeclarationAST> res = new ArrayList<>();
        Token first = nextToken();
        boolean isConst = false;
        if (first.isEnd()) return res;
        if (first.GetType() == TokenType.CONST) {
            isConst = true;
            first = nextToken();
            if (first.isEnd())
                ErrorMsg.Error(first.GetStartPos()+"Incomplete declaration");
        }
        if (first.GetType() == TokenType.INT) {
            while (true) {
                first = nextToken();
                if (first.isEnd())
                    ErrorMsg.Error(first.GetStartPos()+"Incomplete declaration");

                if (first.GetType() != TokenType.IDENTIFIER)
                    ErrorMsg.Error(first.GetStartPos()+"Invalid declaration");


                Token second = nextToken();

                if (second.isEnd())
                    ErrorMsg.Error(first.GetStartPos()+"No semicolon");

                if (second.GetType() == TokenType.ASSIGN_EQUAL) {
                    //如果后面是等号，表示显示初始化，构造变量声明节点
                    VariableDeclarationAST p = new VariableDeclarationAST(isConst, true, first, parseExpression());
                    res.add(p);
                    Token next = nextToken();
                    //遇到分号则跳出循环
                    if (next.GetType() == TokenType.SEMICOLON) {
                        break;
                    }
                } else if (second.GetType() == TokenType.COMMA) {
                    //如果后面是逗号，表示隐式初始化，构造变量声明节点
                    VariableDeclarationAST p = new VariableDeclarationAST(isConst, false, first, null);
                    res.add(p);
                } else if (second.GetType() == TokenType.SEMICOLON) {
                    //遇到分号则跳出循环
                    VariableDeclarationAST p = new VariableDeclarationAST(isConst, false, first, null);
                    res.add(p);
                    break;
                } else {
                    ErrorMsg.Error(first.GetStartPos()+"No semicolon");
                }
            }
        } else {
            ErrorMsg.Error(first.GetStartPos()+"Invalid specifier");
        }
        return res;
    }

    //函数声明
    private FunctionDeclarationAST parseFunctionDefinition() {
        FunctionDeclarationAST res = new FunctionDeclarationAST();
        Token first = nextToken();
        if (first.GetType() == TokenType.VOID || first.GetType() == TokenType.INT) {
            Token identifier = nextToken();
            if (identifier.isEnd())
                ErrorMsg.Error(first.GetStartPos()+"Incomplete declaration");

            if (identifier.GetType() != TokenType.IDENTIFIER)
                ErrorMsg.Error(first.GetStartPos()+"Invalid declaration");

            res.setReturnType(first.GetType());
            res.setIdentifier(identifier);

            Token second = nextToken();
            if (second.GetType() != TokenType.LEFT_PARENT) {
                ErrorMsg.Error(first.GetStartPos()+"Variable declaration after function");

            }

            //处理parameter-clause
            //<parameter-clause> ::=
            //    '(' [<parameter-declaration-list>] ')'
            //<parameter-declaration-list> ::=
            //    <parameter-declaration>{','<parameter-declaration>}
            //<parameter-declaration> ::=
            //    [<const-qualifier>]<type-specifier><identifier>
            second = nextToken();
            if (second.GetType() != TokenType.RIGHT_PARENT) {
                unreadToken();
                while (true) {
                    Token next = nextToken();
                    boolean isConst = false;
                    if (next.GetType() == TokenType.CONST) {
                        isConst = true;
                        next = nextToken();
                        if (next.isEnd())
                            ErrorMsg.Error(first.GetStartPos()+"Incomplete declaration");
                    }
                    if (next.GetType() == TokenType.INT || next.GetType() == TokenType.VOID) {
                        next = nextToken();
                        if (next.GetType() != TokenType.IDENTIFIER) {
                            ErrorMsg.Error(first.GetStartPos()+"Incomplete declaration");
                        }
                        res.add(new VariableDeclarationAST(isConst, next));
                    } else {
                        ErrorMsg.Error(first.GetStartPos()+"Invalid specifier");
                    }
                    next = nextToken();
                    if (next.GetType() == TokenType.RIGHT_PARENT) break;
                    else if (next.GetType() == TokenType.COMMA){
                        continue;
                    }
                    else
                        ErrorMsg.Error(first.GetStartPos()+"Invalid declaration");
                }
            }
            //处理compound-statement
            res.setCompoundStatements(parseCompoundStatement());
        } else {
            ErrorMsg.Error(first.GetStartPos()+"Invalid specifier");

        }
        return res;
    }


    //表达式
    private ExpressionAST parseExpression() {
        ExpressionAST res=new ExpressionAST();
        res.add(parseMultiExpression());
        Token first = nextToken();
        while (first.GetType() == TokenType.PLUS|| first.GetType() == TokenType.MINUS) {
            res.add(parseMultiExpression());
            res.add(first.GetType());
            first = nextToken();
        }
        unreadToken();
        return res;
    }

    //项表达式
    private MultiplicativeExpressionAST parseMultiExpression() {
        MultiplicativeExpressionAST res=new MultiplicativeExpressionAST();
        res.add(parseUnaryExpression());
        Token first = nextToken();
        while (first.GetType() == TokenType.MULTIPLY || first.GetType() ==TokenType.DIVIDE) {
            res.add(parseUnaryExpression());
            res.add(first.GetType());
            first = nextToken();
        }
        unreadToken();
        return res;
    }

    //一元表达式
    private UnaryExpressionAST parseUnaryExpression() {
        Token first = nextToken();
        if (first.GetType() == TokenType.PLUS || first.GetType() ==TokenType.MINUS) {
            return new UnaryExpressionAST(first.GetType(), parsePrimaryExpression());
        } else {
            unreadToken();
            return new UnaryExpressionAST(TokenType.PLUS, parsePrimaryExpression());
        }
    }

    //基表达式
    private PrimaryExpressionAST parsePrimaryExpression() {
        Token first = nextToken();
        if (first.GetType() == TokenType.LEFT_PARENT) {
            PrimaryExpressionAST res = new PrimaryExpressionAST(parseExpression());
            Token second = nextToken();
            if (second.GetType() != TokenType.RIGHT_PARENT) {
                ErrorMsg.Error(first.GetStartPos()+"Incomplete parenthesis");
            }
            return res;
        } else if (first.GetType() == TokenType.INTEGER) {
            return new PrimaryExpressionAST(first);
        } else if (first.GetType() == TokenType.IDENTIFIER) {
            Token second = nextToken();
            unreadToken();
            if (second.GetType() == TokenType.LEFT_PARENT) {
                unreadToken();
                return new PrimaryExpressionAST(parseFunctionCall());
            } else {
                return new PrimaryExpressionAST(first);
            }
        } else {
            ErrorMsg.Error(first.GetStartPos()+"Invalid expression");
        }
        return null;
    }

    //函数调用表达式
    private FunctionCallAST parseFunctionCall() {
        Token identifier = nextToken();
        FunctionCallAST res=new FunctionCallAST(identifier);

        Token next = nextToken();
        if (next.GetType() != TokenType.LEFT_PARENT)
            ErrorMsg.Error(next.GetStartPos()+"Invalid function call");

        next = nextToken();
        if(next.GetType() != TokenType.RIGHT_PARENT) unreadToken();
        while (next.GetType() != TokenType.RIGHT_PARENT) {
            res.add(parseExpression());
            next = nextToken();
            if (next.GetType() != TokenType.COMMA && next.GetType() != TokenType.RIGHT_PARENT)
                ErrorMsg.Error(next.GetStartPos()+"Invalid function call");
        }
        return res;
    }

    //复合语句
    private CompoundStatementAST parseCompoundStatement() {
        Token first = nextToken();
        if (first.GetType() != TokenType.LEFT_BRACE)
            ErrorMsg.Error(first.GetStartPos()+"No left brace");
        CompoundStatementAST res=new CompoundStatementAST();
        while (true) {
            Token next = nextToken();
            unreadToken();
            if (next.GetType() != TokenType.CONST && next.GetType() != TokenType.INT &&
                    next.GetType() != TokenType.VOID)
                break;
            ArrayList<VariableDeclarationAST> p = parseVariableDeclaration();
            for (VariableDeclarationAST v:p) res.add(v);
        }
        while (true) {
            Token next = nextToken();
            if (next.GetType() == RIGHT_BRACE)
                break;
            unreadToken();
            res.add(parseStatement());
        }
        return res;
    }

    //语句
    private StatementAST parseStatement() {
        StatementAST res=new StatementAST();
        TokenType type = nextToken().GetType();
        switch (type) {
            case LEFT_BRACE: {
                while (true) {
                    Token next = nextToken();
                    if (next.GetType() == RIGHT_BRACE)
                        break;
                    unreadToken();
                    res.add(parseStatement());
                }
                break;
            }
            case IF: {
                unreadToken();
                res.setSingleStatement(parseConditionStatement());
                break;
            }
            case WHILE: {
                unreadToken();
                res.setSingleStatement(parseLoopStatement());
                break;
            }
            case RETURN: {
                unreadToken();
                res.setSingleStatement(parseJumpStatement());
                break;
            }
            case PRINT: {
                unreadToken();
                res.setSingleStatement(parsePrintStatement());
                break;
            }
            case SCAN: {
                unreadToken();
                res.setSingleStatement(parseScanStatement());
                break;
            }
            case IDENTIFIER: {
                Token next = nextToken();
                unreadToken();
                unreadToken();
                if (next.GetType() == TokenType.ASSIGN_EQUAL) {
                    res.setSingleStatement(parseAssignExpression());
                } else if (next.GetType() == TokenType.LEFT_PARENT) {
                    res.setSingleStatement(parseFunctionCall());
                } else {
                    ErrorMsg.Error(next.GetStartPos()+"Invalid statement");
                }
                break;
            }
            case SEMICOLON: {
                return null;
            }
            default:
                ErrorMsg.Error("Invalid statement");
        }
        return res;
    }

    //条件语句
    private ConditionStatementAST parseConditionStatement() {
        nextToken(); //吃掉if
        Token first = nextToken();
        if (first.GetType() != TokenType.LEFT_PARENT)
            ErrorMsg.Error(first.GetStartPos()+"Invalid condition");

        ExpressionAST left = parseExpression();
        // Not sure the token type
        TokenType type = NUL;
        ExpressionAST right = null;

        TokenType t = nextToken().GetType();
        if (t == TokenType.LESS || t == TokenType.LE
                || t == TokenType.GREATER || t == TokenType.GE
                || t == TokenType.NOT_EQUAL || t == TokenType.EQUAL) {
            type = t;
            right = parseExpression();
        } else unreadToken();

        first = nextToken();
        if (first.GetType() != TokenType.RIGHT_PARENT)
            ErrorMsg.Error(first.GetStartPos()+"Invalid condition");

        StatementAST ifStatement = parseStatement();
        StatementAST elseStatement = null;

        first = nextToken();
        if (first.GetType() == TokenType.ELSE)
            elseStatement = parseStatement();
        else unreadToken();

        return new ConditionStatementAST(left, type, right, ifStatement, elseStatement);
    }

    //循环语句
    private LoopStatementAST parseLoopStatement() {
        nextToken(); //吃掉while
        Token first = nextToken();
        if (first.GetType() != TokenType.LEFT_PARENT)
            ErrorMsg.Error(first.GetStartPos()+"Invalid condition");

        ExpressionAST left = parseExpression();
        TokenType type = NUL;
        ExpressionAST right = null;

        TokenType t = nextToken().GetType();
        if (t == TokenType.LESS || t == TokenType.LE
                || t == TokenType.GREATER || t == TokenType.GE
                || t == TokenType.NOT_EQUAL || t == TokenType.EQUAL) {
            type = t;
            right = parseExpression();
        } else unreadToken();

        first = nextToken();
        if (first.GetType() != TokenType.RIGHT_PARENT)
            ErrorMsg.Error(first.GetStartPos()+"Invalid condition");

        StatementAST loopStatement = parseStatement();

        return new LoopStatementAST(left, type, right, loopStatement);
    }

    //返回语句
    private JumpStatementAST parseJumpStatement() {
        Token r = nextToken();
        Token first = nextToken();
        if (first.GetType() == TokenType.SEMICOLON) {
            return new JumpStatementAST(r.GetStartPos(),null);
        } else {
            unreadToken();
            ExpressionAST res = parseExpression();
            first = nextToken();
            if (first.GetType() != TokenType.SEMICOLON)
                ErrorMsg.Error(first.GetStartPos()+"No semicolon");
            return new JumpStatementAST(r.GetStartPos(),res);
        }
    }

    //打印语句
    private PrintStatementAST parsePrintStatement() {
        nextToken(); // 吃掉print
        Token next = nextToken();
        if (next.GetType() != TokenType.LEFT_PARENT)
            ErrorMsg.Error(next.GetStartPos()+"Invalid print");

        PrintStatementAST res=new PrintStatementAST();

        next = nextToken();
        if (next.GetType() != TokenType.RIGHT_PARENT) unreadToken();
        while (next.GetType() != TokenType.RIGHT_PARENT) {
            res.add(parseExpression());
            next = nextToken();
            if (next.GetType() != TokenType.COMMA && next.GetType() != TokenType.RIGHT_PARENT)
                ErrorMsg.Error(next.GetStartPos()+"Invalid print");
        }

        next = nextToken();
        if (next.GetType() != TokenType.SEMICOLON)
            ErrorMsg.Error(next.GetStartPos()+"No semicolon");
        return res;
    }

    //输入语句
    private ScanStatementAST parseScanStatement() {
        nextToken(); //吃掉scan

        Token first = nextToken();
        if (first.GetType() != TokenType.LEFT_PARENT)
            ErrorMsg.Error(first.GetStartPos()+"Invalid scan");

        first = nextToken();
        if (first.GetType() != TokenType.IDENTIFIER)
            ErrorMsg.Error(first.GetStartPos()+"Invalid scan");

        Token identifier = first;

        first = nextToken();
        if (first.GetType() != TokenType.RIGHT_PARENT)
            ErrorMsg.Error(first.GetStartPos()+"Invalid scan");

        first = nextToken();
        if (first.GetType() != TokenType.SEMICOLON)
            ErrorMsg.Error(first.GetStartPos()+"No semicolon");

        return new ScanStatementAST(identifier);
    }

    //赋值语句
    private AssignExpressionAST parseAssignExpression() {
        Token identifier = nextToken();
        Token next = nextToken();
        if (next.GetType() != TokenType.ASSIGN_EQUAL)
            ErrorMsg.Error(next.GetStartPos()+"Invalid assignment");
        ExpressionAST assignExpr = parseExpression();
        next = nextToken();
        if (next.GetType() != TokenType.SEMICOLON)
            ErrorMsg.Error(next.GetStartPos()+"No semicolon");

        return new AssignExpressionAST(identifier, assignExpr);
    }


}
