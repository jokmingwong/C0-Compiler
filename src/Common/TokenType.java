package Common;

public enum TokenType {
    NUL, // Cannot be analysed
    IDENTIFIER, // Identifier
    INTEGER,
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

    // And operator
    PLUS, //+
    MINUS, // -
    MULTIPLY, // *
    DIVIDE, // \
    EQUAL, // =
    ASSIGN_EQUAL,
    LESS,
    LE,
    GREATER,
    GE,
    NOT_EQUAL,
    LEFT_PARENT, // (
    RIGHT_PARENT, // )
    LEFT_BRACE, // {
    RIGHT_BRACE,  // }
    COMMA, // ,
    SEMICOLON // ;
}
