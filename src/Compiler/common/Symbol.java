package Compiler.common;

/**
 * Title：符号表
 * Description：符号类型名字
 * Created by Adam on 12/12/2019.
 */
public enum Symbol {
    NUL, // cannot be analysed
    IDENTIFIER, // identifier
    NUMBER, // number
    CONST,
    VOID,
    INT,
    CHAR,
    DOUBLE,
    STRUCT,
    IF,
    ELSE,
    SWITCH,
    CASE,
    DEFAULT,
    WHILE,
    FOR,
    DO,
    RETURN,
    BREAK,
    CONTINUE,
    PRINT,
    SCAN,
    MAIN,
    END,
    // And operator
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    EQUAL, //加减乘除 等于
    LEFT_BRACKET, // (
    RIGHT_BRACKET, // )
    LEFT_BRACE, // {
    RIGHT_BRACE,  // }
    COMMA, // ,
    SEMICOLON // ;
}
