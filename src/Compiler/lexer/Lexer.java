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
 * Title：词法分析器
 * Description：分析源程序的每一个字符，组合成单词，将单词转化成符号
 * Created by Adam on 13/12/2019
 */
public class Lexer {
    // Store every C0 in the list
    private List<String> sourceCodeLineList;

    // The number of the C0 line
    private int lineSize = 0;

    // Max line of the code
    private static final int MAX_LINE_SIZE = 300;

    // -1 means end
    private int lineNumber;

    // -1 means end
    private int position;

    private String currentLine;
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
        return sourceCodeLineList;
    }

    /**
     * Open a file to analyse
     * @param pathname the name of the file
     * @throw Exception
     */
    public void openFile(String pathname) throws Exception{
        File file = new File(pathname);
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        sourceCodeLineList = new ArrayList<>();
        while((line = bufferedReader.readLine())!=null) {
            sourceCodeLineList.add(line);
        }
        lineSize = sourceCodeLineList.size();
        if (lineSize > MAX_LINE_SIZE) {
            lineSize = 0;
            throw new Exception("The code is too long!");
        }
        if (lineSize == 0) {
            lineSize = 0;
            throw new Exception("The code cannot be empty!");
        }
        currentLine = sourceCodeLineList.get(0);
    }

    // 前移位置
    private void nextChar() {
        // Change the line
        if (position >= currentLine.length()-1) {
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
        currentLine = sourceCodeLineList.get(lineNumber);
    }

    /**
     * Get a new word
     * @return new word
     * @throws Exception
     */
    public Word getToken() throws Exception {
        Word word = new Word();
        if (lineNumber ==-1 && position==-1) {
            word.setType(Symbol.END); // Stop
            return word;
        }
        StringBuilder wordValue = new StringBuilder("");
        // Remove the empty line
        while (currentLine.length()==0) {
            lineNumber++;
            currentLine = sourceCodeLineList.get(lineNumber);
        }
        char currentChar = currentLine.charAt(position);
        // Filter the space and the tab
        while (currentChar == ' ' || currentChar == '\t') {
            nextChar();
            currentChar = currentLine.charAt(position);
        }
        //System.out.println("("+ lineNumber + "," + position +")"+" 字符：" + currentLine.charAt(position));
        if (Character.isLetter(currentChar))
        {
            // identifier
            do
            {
                wordValue.append(currentChar);
                nextChar();
                if (lineNumber ==-1 && position==-1) {
                    if (!("".equals(wordValue.toString()))) {
                        word.setType(Symbol.IDENTIFIER);
                        word.setValue(wordValue.toString());
                    } else {
                        word.setType(Symbol.END); //终止位置，终止
                    }
                    return word;
                }
                currentChar = currentLine.charAt(position);
            } while (currentChar >= 'a'&&currentChar <= 'z' || currentChar >= '0'&&currentChar <= '9');
            
            //Judge it is keyword
            word.setType(keywordsMap.getOrDefault(wordValue.toString(),Symbol.IDENTIFIER));
        } 
        else {
            // digit?
            if (Character.isDigit(currentChar))
            {
                do
                {
                    wordValue.append(currentChar);
                    nextChar();
                    if (lineNumber ==-1 && position==-1) {
                        word.setType(Symbol.END);
                        return word;
                    }
                    currentChar = currentLine.charAt(position);
                } while (Character.isDigit(currentChar));
                word.setType(Symbol.NUMBER);
            }
            else {
                // Check weather the char is operator
                wordValue.append(currentChar);
                word.setType(operatorsMap.getOrDefault(wordValue.toString(),Symbol.NUL));
                nextChar();
            }
        }
        word.setValue(wordValue.toString());
        return word;
    }
    public String getPosition() {
        return "The line:" + (lineNumber+1);
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
