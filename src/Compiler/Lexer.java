package Compiler;

import Common.ErrorMsg;
import Common.Pair;
import Common.Token;
import Common.TokenType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Character.*;

/**
 * Title：Lexer
 * Created by Adam on 13/12/2019
 */
public class Lexer {
    private boolean _isInitialized;
    private ArrayList<String> _lines_buffer;
    private Pair<Integer, Integer> _ptr;

    public Lexer() {
        _lines_buffer=new ArrayList<>();
    }
    
    private static boolean isSpace(char ch){
        return isSpaceChar(ch)||ch=='\n'||ch=='\t'||ch=='\r';
    }


    private enum DFAState {
        INIT_STATE,
        INTEGER_STATE, IDENTIFIER_STATE, ZERO_STATE,
        HEX_STATE,
        PLUS_STATE, MINUS_STATE,
        MULTIPLY_STATE, DIVIDE_STATE,
        ASSIGN_EQUAL_STATE,
        LESS_STATE, LE_STATE, GREATER_STATE,
        GE_STATE, NOT_EQUAL_STATE, EQUAL_STATE,
        COMMA_STATE,
        LEFT_PARENTHESIS_STATE, RIGHT_PARENTHESIS_STATE,
        LEFT_BRACE_STATE, RIGHT_BRACE_STATE,
        SEMICOLON_STATE,
    }

    private char nextChar() {
        if (isEOF()) {
            return ' ';
        }
        char result = _lines_buffer.get(_ptr.getFirst()).charAt(_ptr.getSecond());
        _ptr = nextPos();
        return result;
    }

    private void unreadLast() {
        _ptr = previousPos();
    }


    public Token nextToken() {
        StringBuilder ss = new StringBuilder();
        // Todo:is (0,0) right?
        Pair<Integer, Integer> pos=new Pair<>(0,0);
        DFAState current_state = DFAState.INIT_STATE;
        while (true) {
            char current_char = nextChar();
            switch (current_state) {
                case INIT_STATE: {
                    if (isEOF())
                        return new Token();
                    boolean invalid = false;
                    if (isSpace(current_char)) {
                        current_state = DFAState.INIT_STATE;
                    } else if (isISOControl(current_char)) {
                        invalid = true;
                    } else if (isDigit(current_char)) {
                        if (current_char == '0')
                            current_state = DFAState.ZERO_STATE;
                        else
                            current_state = DFAState.INTEGER_STATE;
                    } else if (isLetter(current_char)) {
                        current_state = DFAState.IDENTIFIER_STATE;
                    } else {
                        switch (current_char) {
                            case '+':
                                current_state = DFAState.PLUS_STATE;
                                break;
                            case '-':
                                current_state = DFAState.MINUS_STATE;
                                break;
                            case '*':
                                current_state = DFAState.MULTIPLY_STATE;
                                break;
                            case '/':
                                current_state = DFAState.DIVIDE_STATE;
                                break;
                            case '(':
                                current_state = DFAState.LEFT_PARENTHESIS_STATE;
                                break;
                            case ')':
                                current_state = DFAState.RIGHT_PARENTHESIS_STATE;
                                break;
                            case '{':
                                current_state = DFAState.LEFT_BRACE_STATE;
                                break;
                            case '}':
                                current_state = DFAState.RIGHT_BRACE_STATE;
                                break;
                            case ',':
                                current_state = DFAState.COMMA_STATE;
                                break;
                            case ';':
                                current_state = DFAState.SEMICOLON_STATE;
                                break;
                            case '>':
                                current_state = DFAState.GREATER_STATE;
                                break;
                            case '<':
                                current_state = DFAState.LESS_STATE;
                                break;
                            case '!':
                                current_state = DFAState.NOT_EQUAL_STATE;
                                break;
                            case '=':
                                current_state = DFAState.ASSIGN_EQUAL_STATE;
                                break;
                            default:
                                invalid = true;
                                break;
                        }
                    }
                    if (current_state != DFAState.INIT_STATE)
                        pos = previousPos();
                    if (invalid) {
                        unreadLast();
                        ErrorMsg.Error( "Invalid identifier");
                    }
                    if (current_state != DFAState.INIT_STATE && (Character.isLetter(current_char) || Character.isDigit(current_char)))
                        ss.append(current_char);
                    break;
                }
                case INTEGER_STATE: {
                    boolean invalid = false;
                    if (isISOControl(current_char) && !isSpace(current_char)) invalid = true;
                    else if (isDigit(current_char)) ss.append(current_char);
                    else {
                        unreadLast();
                        try {
                            String s = ss.toString();
                            int res = Integer.parseInt(s);
                            return new Token(TokenType.INTEGER, res, pos, currentPos());
                        } catch (Exception e) {
                            ErrorMsg.Error(pos + ":Overflow");
                        }
                    }
                    if (invalid) {
                        unreadLast();
                        ErrorMsg.Error(pos + ":Invalid");
                    }
                    break;
                }
                case IDENTIFIER_STATE: {
                    boolean invalid = false;
                    if (isISOControl(current_char) && !isSpace(current_char))
                        invalid = true;
                    else if (isDigit(current_char) || isLetter(current_char))
                        ss.append(current_char);
                    else {
                        unreadLast();
                        try {
                            String s = ss.toString();
                            switch (s) {
                                case "const":
                                    return new Token(TokenType.CONST, s, pos, currentPos());
                                case "int":
                                    return new Token(TokenType.INT, s, pos, currentPos());
                                case "void":
                                    return new Token(TokenType.VOID, s, pos, currentPos());
                                case "char":
                                    return new Token(TokenType.CHAR, s, pos, currentPos());
                                case "double":
                                    return new Token(TokenType.DOUBLE, s, pos, currentPos());
                                case "struct":
                                    return new Token(TokenType.STRUCT, s, pos, currentPos());
                                case "if":
                                    return new Token(TokenType.IF, s, pos, currentPos());
                                case "else":
                                    return new Token(TokenType.ELSE, s, pos, currentPos());
                                case "switch":
                                    return new Token(TokenType.SWITCH, s, pos, currentPos());
                                case "case":
                                    return new Token(TokenType.CASE, s, pos, currentPos());
                                case "default":
                                    return new Token(TokenType.DEFAULT, s, pos, currentPos());
                                case "while":
                                    return new Token(TokenType.WHILE, s, pos, currentPos());
                                case "for":
                                    return new Token(TokenType.FOR, s, pos, currentPos());
                                case "do":
                                    return new Token(TokenType.DO, s, pos, currentPos());
                                case "return":
                                    return new Token(TokenType.RETURN, s, pos, currentPos());
                                case "break":
                                    return new Token(TokenType.BREAK, s, pos, currentPos());
                                case "continue":
                                    return new Token(TokenType.CONTINUE, s, pos, currentPos());
                                case "print":
                                    return new Token(TokenType.PRINT, s, pos, currentPos());
                                case "scan":
                                    return new Token(TokenType.SCAN, s, pos, currentPos());

                                default: {
                                    return new Token(TokenType.IDENTIFIER, s, pos, currentPos());
                                }
                            }
                        } catch (Exception e) {
                            ErrorMsg.Error(pos + ":Invalid");
                        }
                    }
                    break;
                }

                case ZERO_STATE: {
                    if (current_char == 'X' || current_char == 'x') {
                        current_state = DFAState.HEX_STATE;
                        ss.append(current_char);
                        break;
                    } else {
                        unreadLast();
                        return new Token(TokenType.INTEGER, 0, pos, currentPos());
                    }

                }

                case HEX_STATE: {
                    boolean invalid = false;
                    if (isISOControl(current_char) && !isSpace(current_char))
                        invalid = true;
                    else if (isDigit(current_char))
                        ss.append(current_char);
                    else if (current_char == 'a' || current_char == 'A' ||
                            current_char == 'b' || current_char == 'B' ||
                            current_char == 'c' || current_char == 'C' ||
                            current_char == 'd' || current_char == 'D' ||
                            current_char == 'e' || current_char == 'E' ||
                            current_char == 'f' || current_char == 'F')
                        ss.append(current_char);
                    else {
                        unreadLast();
                        String s;
                        s = ss.toString();
                        try {
                            int res = Integer.decode(s);
                            return new Token(TokenType.INTEGER, res, pos, currentPos());
                        } catch (Exception e) {
                            if (s.length() == 2)
                                ErrorMsg.Error(pos + ":Invalid hex number");
                            else
                                ErrorMsg.Error(pos + ":Overflow");
                        }
                    }
                    if (invalid) {
                        unreadLast();
                        ErrorMsg.Error(":Invalid");
                    }
                    break;
                }

                case PLUS_STATE: {
                    unreadLast();
                    return new Token(TokenType.PLUS, '+', pos, currentPos());
                }
                case MINUS_STATE: {
                    unreadLast();
                    return new Token(TokenType.MINUS, '-', pos, currentPos());
                }
                case MULTIPLY_STATE: {
                    unreadLast();
                    return new Token(TokenType.MULTIPLY, '*', pos, currentPos());
                }
                case DIVIDE_STATE: {
                    if (current_char == '/') {
                        while (current_char != '\n') {
                            if (isEOF())
                                ErrorMsg.Error(pos + ":Incomplete Common");
                            current_char = nextChar();
                        }
                        current_state = DFAState.INIT_STATE;
                        break;
                    } else if (current_char == '*') {
                        current_char = nextChar();
                        char next_char = nextChar();
                        while (current_char != '*' || next_char != '/') {
                            if (isEOF())
                                ErrorMsg.Error(pos + ":Incomplete Common");
                            current_char = next_char;
                            next_char = nextChar();
                        }
                        current_state = DFAState.INIT_STATE;
                        break;
                    } else {
                        unreadLast();
                        return new Token(TokenType.DIVIDE, '/', pos, currentPos());
                    }
                }
                case ASSIGN_EQUAL_STATE: {
                    if (current_char == '=') {
                        current_state = DFAState.EQUAL_STATE;
                        break;
                    } else {
                        unreadLast();
                        return new Token(TokenType.ASSIGN_EQUAL, '=', pos, currentPos());
                    }
                }
                case LESS_STATE: {
                    if (current_char == '=') {
                        current_state = DFAState.LE_STATE;
                        break;
                    } else {
                        unreadLast();
                        return new Token(TokenType.LESS, '<', pos, currentPos());
                    }
                }
                case LE_STATE: {
                    unreadLast();
                    return new Token(TokenType.LE, "<=", pos, currentPos());
                }
                case GREATER_STATE: {
                    if (current_char == '=') {
                        current_state = DFAState.GE_STATE;
                        break;
                    } else {
                        unreadLast();
                        return new Token(TokenType.GREATER, '>', pos, currentPos());
                    }
                }
                case GE_STATE: {
                    unreadLast();
                    return new Token(TokenType.GE, ">=", pos, currentPos());
                }
                case NOT_EQUAL_STATE: {
                    if (current_char == '=')
                        return new Token(TokenType.NOT_EQUAL,  "!=", pos, currentPos());
                    else
                        ErrorMsg.Error(pos+"Invalid sign");
                }
                case EQUAL_STATE: {
                    unreadLast();
                    return new Token(TokenType.EQUAL, "==", pos, currentPos());
                }
                case COMMA_STATE: {
                    unreadLast();
                    return new Token(TokenType.COMMA, ',', pos, currentPos());
                }
                case LEFT_PARENTHESIS_STATE: {
                    unreadLast();
                    return new Token(TokenType.LEFT_PARENT, '(', pos, currentPos());
                }
                case RIGHT_PARENTHESIS_STATE: {
                    unreadLast();
                    return new Token(TokenType.RIGHT_PARENT, ')', pos, currentPos());
                }
                case LEFT_BRACE_STATE: {
                    unreadLast();
                    return new Token(TokenType.LEFT_BRACE, '{', pos, currentPos());
                }
                case RIGHT_BRACE_STATE: {
                    unreadLast();
                    return new Token(TokenType.RIGHT_BRACE, '}', pos, currentPos());
                }
                case SEMICOLON_STATE: {
                    unreadLast();
                    return new Token(TokenType.SEMICOLON, ';', pos, currentPos());
                }
                default:
                    ErrorMsg.Error("An exception state");
                    break;

            }
        }
    }


    // TODO:FINISH
    public ArrayList<Token> allTokens(FileReader fr) {
        if (!_isInitialized) readAll(fr);

        ArrayList<Token> result=new ArrayList<>();
        while (true) {
            Token p = nextToken();
            //checkToken(p);
            if(p.GetType()==TokenType.IDENTIFIER){
                String val=p.GetValueString();
                if(isDigit(val.charAt(0)))
                    ErrorMsg.Error("Invalid identifier");
            }
            if (p.isEnd()) return result;
            result.add(p);
        }
    }

    // Todo:debug stream
    private void readAll(FileReader fr) {
        if (_isInitialized)
            return;
        try {
            BufferedReader bf = new BufferedReader(fr);
            String str;
            while ((str = bf.readLine()) != null) {
                _lines_buffer.add(str+"\n");
            }
        }catch (IOException e){
            ErrorMsg.Error("Read error");
        }

        _isInitialized = true;
        _ptr = new Pair<>(0, 0);
    }

    private Pair<Integer, Integer> nextPos() {
        if (_ptr.getFirst() >= _lines_buffer.size())
            ErrorMsg.Error("EOF");
        if (_ptr.getSecond() == _lines_buffer.get(_ptr.getFirst()).length() - 1)
            return new Pair<>(_ptr.getFirst() + 1, 0);
        else
            return new Pair<>(_ptr.getFirst(), _ptr.getSecond() + 1);
    }

    private Pair<Integer, Integer> currentPos() {
        return _ptr;
    }

    private Pair<Integer, Integer> previousPos() {
        if (_ptr.getFirst() == 0 && _ptr.getSecond() == 0)
            ErrorMsg.Error("previous position from beginning");
        if (_ptr.getSecond() == 0)
            return new Pair<>(_ptr.getFirst() - 1, _lines_buffer.get(_ptr.getFirst() - 1).length() - 1);
        else
            return new Pair<>(_ptr.getFirst(), _ptr.getSecond() - 1);
    }

    private boolean isEOF() {
        return _ptr.getFirst() >= _lines_buffer.size();
    }
}
