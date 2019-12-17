package Compiler.lexer;

import Compiler.common.Symbol;
import Compiler.common.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title：Lexer
 * Created by Adam on 13/12/2019
 */
public class Lexer {
    // Store every C0 code in a list
    private List<String> CodeLineList;

    // The number of the C0 line
    private int lineSize = 0;

    // Max line of the code
    private static final int MAX_LINE_SIZE = 5000;

    // -1 means end
    private int lineNumber;
    private char currentChar;

    // -1 means end
    private int position;

    private String currentLineContent;
    // A map to store keywords and the all operators
    private Map<String,Symbol> keywordsMap;
    private Map<String,Symbol> operatorsMap;
    public Lexer() {
        lineNumber = 0;
        position = 0;
        // Initialization of all the keywords
        keywordsMap = new HashMap<>();
        keywordsMap.put("const",Symbol.CONST);
        keywordsMap.put("void",Symbol.VOID);
        keywordsMap.put("int",Symbol.INT);
        keywordsMap.put("char",Symbol.CHAR);
        keywordsMap.put("double",Symbol.DOUBLE);
        keywordsMap.put("struct",Symbol.STRUCT);
        keywordsMap.put("if",Symbol.IF);
        keywordsMap.put("else",Symbol.ELSE);
        keywordsMap.put("switch",Symbol.SWITCH);
        keywordsMap.put("case",Symbol.CASE);
        keywordsMap.put("default",Symbol.DEFAULT);
        keywordsMap.put("while",Symbol.WHILE);
        keywordsMap.put("for",Symbol.FOR);
        keywordsMap.put("do",Symbol.DO);
        keywordsMap.put("return",Symbol.RETURN);
        keywordsMap.put("break",Symbol.BREAK);
        keywordsMap.put("continue",Symbol.CONTINUE);
        keywordsMap.put("printf",Symbol.PRINT);
        keywordsMap.put("scan",Symbol.SCAN);

        // Add a main symbol to start the main function
        keywordsMap.put("main",Symbol.MAIN);


        // Operator
        operatorsMap = new HashMap<>();
        operatorsMap.put("+",Symbol.PLUS);
        operatorsMap.put("-",Symbol.MINUS);
        operatorsMap.put("*",Symbol.MULTIPLY);
        operatorsMap.put("/",Symbol.DIVIDE);
        operatorsMap.put("=",Symbol.EQUAL);
        operatorsMap.put("(",Symbol.LEFT_BRACKET);
        operatorsMap.put(")",Symbol.RIGHT_BRACKET);
        operatorsMap.put("{",Symbol.LEFT_BRACE);
        operatorsMap.put("}",Symbol.RIGHT_BRACE);
        operatorsMap.put(",",Symbol.COMMA);
        operatorsMap.put(";",Symbol.SEMICOLON);


    }
    public List<String> getSourceCodeLineList() {
        return CodeLineList;
    }

    /**
     * Open a file to analyse
     * @param pathname the name of the file
     */
    public void openFile(String pathname) throws Exception{
        File file = new File(pathname);
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        CodeLineList = new ArrayList<>();
        while((line = bufferedReader.readLine())!=null) {
            CodeLineList.add(line);
        }
        lineSize = CodeLineList.size();
        if (lineSize > MAX_LINE_SIZE) {
            lineSize = 0;
            throw new Exception("The code is too long!");
        }
        if (lineSize == 0) {
            lineSize = 0;
            throw new Exception("The code cannot be empty!");
        }
        // Get the first line of the code
        currentLineContent = CodeLineList.get(0);
    }

    // Move to next char
    private void moveToNextChar() {
        // Change the line
        if (position >= currentLineContent.length()-1) {
            lineNumber++;
            position = 0;
        }
        // In the same line, move to the next char
        else {
            position++;
        }

        if (lineNumber >= lineSize) {
            lineNumber = -1;
            position = -1;
            return;
        }
        // In case that code change the line
        currentLineContent = CodeLineList.get(lineNumber);
    }

    private char getCurrentChar(){
        return currentLineContent.charAt(position);
    }

    private char getNextChar(){
        int l=lineNumber,p=position;
        if (p >= currentLineContent.length()-1) {
            return '\n';
        }
        // In the same line, move to the next char
        else {
            return currentLineContent.charAt(p+1);
        }
    }

    /**
     * Analyse a token form a line string
     * @return new token
     */
    public Word getToken() throws Exception {
        Word word = new Word();
        // Judge weather the code ends
        if (lineNumber ==-1 && position==-1) {
            word.setType(Symbol.END);
            return word;
        }
        StringBuilder wordValue = new StringBuilder("");
        // Remove the empty line
        while (currentLineContent.length()==0) {
            lineNumber++;
            currentLineContent = CodeLineList.get(lineNumber);
        }
        char currentChar = getCurrentChar();
        // Filter the space and the tab, do not solve the '\n'
        while (currentChar == ' ' || currentChar == '\t' ||currentChar=='\r') {
            moveToNextChar();
            currentChar = getCurrentChar();
        }

        if (Character.isLetter(currentChar))
        {
            // identifier
            do
            {
                wordValue.append(currentChar);
                moveToNextChar();
                if (lineNumber ==-1 && position==-1) {
                    // Not empty
                    if (!("".equals(wordValue.toString()))) {
                        word.setType(Symbol.IDENTIFIER);
                        word.setValue(wordValue.toString());
                    } else {
                        word.setType(Symbol.END);
                    }
                    return word;
                }
                currentChar = getCurrentChar();
            } while (Character.isLetter(currentChar) || Character.isDigit(currentChar));
            
            //Judge weather is keyword
            word.setType(keywordsMap.getOrDefault(wordValue.toString(),Symbol.IDENTIFIER));
        } 
        else {
            // digit?
            if (Character.isDigit(currentChar))
            {
                if(currentChar=='0' && (getNextChar()=='x'||getNextChar()=='X')) {
                    wordValue.append(currentChar);
                    // Add char 'x' or 'X' into the number
                    moveToNextChar();
                    currentChar=getCurrentChar();
                    wordValue.append(currentChar);

                    // Add the number behind the char 'X' or 'x'
                    moveToNextChar();
                    currentChar=getCurrentChar();
                    while (Character.isDigit(currentChar)) {
                        wordValue.append(currentChar);
                        moveToNextChar();
                        if(lineNumber==-1 && position == -1) {
                            word.setType(Symbol.END);
                            return word;
                        }
                        currentChar=getCurrentChar();
                    }
                }
                else {
                    do {
                        wordValue.append(currentChar);
                        moveToNextChar();
                        // Maybe the code is end
                        if (lineNumber == -1 && position == -1) {
                            word.setType(Symbol.END);
                            return word;
                        }
                        currentChar = getCurrentChar();
                    } while (Character.isDigit(currentChar));
                }
                word.setType(Symbol.NUMBER);
            }
            else {
                // Check the char is operator
                wordValue.append(currentChar);
                word.setType(operatorsMap.getOrDefault(wordValue.toString(),Symbol.NUL));
                moveToNextChar();
            }
        }
        word.setValue(wordValue.toString());
        return word;
    }
    /*
    public String getPosition() {
        return "The line:" + (lineNumber+1);
    }
    */

    public int getLineNumber() {
        return lineNumber;
    }
}
