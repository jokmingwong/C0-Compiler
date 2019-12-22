package Compiler.parser;

import Compiler.common.TokenType;
import Compiler.common.ErrorMsg;
import Compiler.Instruction.Instruction;
import Compiler.Instruction.InstructionType;
import Compiler.lexer.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Compiler.common.TokenType.*;

/**
 * Title：ProgramParser
 * Description： <C0-program> ::={<variable-declaration>}{<function-definition>}
 * Warning: 注意生成的指令是从位置1开始的 and call main function
 * Created by Adam
 */
public class ProgramParser {
    private Lexer lexer;
    private Word currentWord;
    private List<ErrorMsg> ErrorMsgList;

    private int currentLayer=0;

    // Note the function call
    private List<FunctionCall> functionCallList;

    // Variable table
    private Map<String, Variable> variableMap;

    // Function table
    private Map<String, Function> functionMap;





    //TODO 考虑是否在开始加一条跳转到主函数指令
    /**
     * 生成的指令，指令从位置1开始
     */
    private List<Instruction> generatedInstructionList;
    // Count the number of global variable
    private int globalCount = 0;
    
    // 用来判断是否属于某一语法单位
    private boolean[] statementBeginSymbolFlags;  // 语句开始符号集
    private boolean[] factorBeginSymbolFlags; //因子开始符号集

    public List<String> getSourceCodeLineList(){
        return lexer.getSourceCodeLineList();
    }

    public List<ErrorMsg> getWrongList() {
        return ErrorMsgList;
    }

    public List<FunctionCall> getFunctionCallList() {
        return functionCallList;
    }



    public Map<String, Variable> getVariableMap() {
        return variableMap;
    }

    public Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    public List<Instruction> getGeneratedInstructionList() {
        return generatedInstructionList;
    }

    public ProgramParser(String pathname) throws Exception {
        // Initialization
        lexer = new Lexer();
        ErrorMsgList = new ArrayList<>();
        functionCallList = new ArrayList<>();
        variableMap = new HashMap<>();
        functionMap = new HashMap<>();
        generatedInstructionList = new ArrayList<>();
        //为了使产生的代码从下标1开始，添加一个占位元素占据下标0位置
        generatedInstructionList.add(new Instruction());

        // Generate a 'jmp' instruction to the function main, and when we find the address of main
        // write it in this instructions
        generatedInstructionList.add(new Instruction(InstructionType.JMP, 0, -1));
        lexer.analyseFile(pathname);

        statementBeginSymbolFlags = new boolean[34];
        factorBeginSymbolFlags = new boolean[34];

        // Give the value true of the first set of statement
        statementBeginSymbolFlags[TokenType.IDENTIFIER.ordinal()]= true;
        statementBeginSymbolFlags[TokenType.CONST.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.VOID.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.INT.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.CHAR.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.DOUBLE.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.STRUCT.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.IF.ordinal()]= true;
        statementBeginSymbolFlags[TokenType.SWITCH.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.WHILE.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.FOR.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.DO.ordinal()]=true;
        statementBeginSymbolFlags[TokenType.RETURN.ordinal()]= true;
        statementBeginSymbolFlags[TokenType.SCAN.ordinal()]= true;
        statementBeginSymbolFlags[TokenType.PRINT.ordinal()]= true;

        // Give the value true of the first set of factor
        factorBeginSymbolFlags[TokenType.IDENTIFIER.ordinal()] = true;
        factorBeginSymbolFlags[TokenType.INTEGER.ordinal()] = true;
        factorBeginSymbolFlags[TokenType.LEFT_BRACKET.ordinal()] = true;

    }

    // Generate the instructions, and return the address of new instructions
    private int generateInstruction(InstructionType type, int layer, int third) {
        Instruction instruction = new Instruction();
        instruction.setName(type);
        instruction.setLayer(layer);
        instruction.setThird(third);
        generatedInstructionList.add(instruction);
        return generatedInstructionList.indexOf(instruction);
    }

    // <因子> ::= [<符号>]( <标识符> | <无符号整数> | '('<表达式>')' )
    private void parseFactor(String factorName) throws Exception {
        boolean isMinus=false;
        // Plus or Minus
        if(currentWord.getType()==PLUS){
            currentWord=lexer.nextToken();
        }
        else if(currentWord.getType()==MINUS){
            isMinus=true;
            currentWord=lexer.nextToken();
        }

        // End till meet the non-factor
        while (factorBeginSymbolFlags[currentWord.getType().ordinal()])
        {
            switch (currentWord.getType()) {
                // Variable of function call
                case IDENTIFIER:{
                    // Save the value
                    String val = currentWord.getValue();
                    currentWord = lexer.nextToken();
                    // Meet the '(', it's a function
                    if (currentWord.getType() == TokenType.LEFT_BRACKET) {
                        currentWord = lexer.nextToken();
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        }
                        else {
                            int position = generateInstruction(InstructionType.CAL, 0, -1);
                            FunctionCall functionCall = new FunctionCall(val, position,true, lexer.getLineNumber()+1);
                            functionCallList.add(functionCall);
                        }
                        currentWord = lexer.nextToken();
                    }
                    else {
                        // variable
                        Variable variable = variableMap.get(val);
                        if (variable != null && (variable.getScope().equals(factorName)||variable.getScope().equals("global"))) {
                            if (variable.getScope().equals(factorName)) {
                                generateInstruction(InstructionType.LOD, 1,variable.getAddress()); //当前层
                            }
                            else {
                                generateInstruction(InstructionType.LOD, 0,variable.getAddress()); //全局变量，外层
                            }
                        }
                        else {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 7, "变量未声明或不在作用域内"));
                        }
                    }
                    break;
                }
                case INTEGER: {
                    generateInstruction(InstructionType.LIT, 0, isMinus?-Integer.parseInt(currentWord.getValue()):Integer.parseInt(currentWord.getValue()));
                    currentWord = lexer.nextToken();
                    break;
                }
                case LEFT_BRACKET: {
                    currentWord = lexer.nextToken();
                    // Expression
                    parseExpression(factorName);
                    // ')'
                    if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                    }
                    currentWord = lexer.nextToken();
                    break;
                }
            }
        }
    }

    // <项> :: = <因子>{ <乘法型运算符><因子> }
    private void parseItem(String itemName) throws Exception {
        TokenType t;
        // <factor>
        parseFactor(itemName);
        // {<multiply><factor>/<divide><factor>}
        while (currentWord.getType() == TokenType.MULTIPLY || currentWord.getType() == TokenType.DIVIDE) {
            t = currentWord.getType();
            currentWord = lexer.nextToken();
            parseFactor(itemName);
            if (t == TokenType.MULTIPLY) {
                // Generate the instructions of multiply
                generateInstruction(InstructionType.MUL, 0, 0);
            } else { // Generate the instructions of divide
                generateInstruction(InstructionType.DIV, 0, 0);
            }
        }
    }

    // <表达式> ::= <项>{<加法型运算符><项>}
    private void parseExpression(String expressionName) throws Exception {

        // <item>
        parseItem(expressionName);
        // <plus/minus><item>
        TokenType t;
        while (currentWord.getType() ==  TokenType.PLUS || currentWord.getType() == TokenType.MINUS) {
            t = currentWord.getType();
            currentWord = lexer.nextToken();

            parseItem(expressionName);
            if (t == TokenType.PLUS)
                generateInstruction(InstructionType.ADD, 0, 0);
            else
                generateInstruction(InstructionType.SUB, 0, 0);

        }
    }


    /*
    <statement-seq> ::= {<statement>}
    <statement> ::=
     '{' <statement-seq> '}'
    |<condition-statement>
    |<loop-statement>
    |<jump-statement>
    |<print-statement>
    |<scan-statement>
    |<assignment-expression>';'
    |<function-call>';'
    |';'
     */
    /*处理语句
    <语句>-> <条件语句>｜<循环语句> | '{'<语句序列>'}' | <自定义函数调用语句> | <赋值语句> | <返回语句> | <读语句> | <写语句> | ;
    返回 最后一次生成的指令地址（在generatedInstructionList中的位置）
    */
    private int parseStatement(String statement) throws Exception {
        int lastInstructionPosition = -1;
        while(statementBeginSymbolFlags[currentWord.getType().ordinal()]) {  //循环直到不是语句开始符号
            switch (currentWord.getType()) {
                //<condition-statement> :: 'if' '(' <condition> ')' <statement> ['else' <statement>]
                case IF: {
                    int jpcInstructionPosition;
                    int jmpInstructionPosition;
                    int elseFinishPosition;

                    // Judge the operator '('
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.LEFT_BRACKET ) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing '('"));
                        //throw new Exception("Missing (");
                    } 
                    else {
                        currentWord = lexer.nextToken();
                        // xxx: if(xxx)
                        parseExpression(statement);
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET ) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing ')'"));
                        } 
                        else {
                            //生成JPC指令，记录下来此条指令的地址(用于回填)
                            jpcInstructionPosition = generateInstruction(InstructionType.JPC, 0, -1);
                            currentWord = lexer.nextToken();
                            if (currentWord.getType() != TokenType.LEFT_BRACE) {
                                ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing'{'"));
                            } 
                            else {
                                currentWord = lexer.nextToken();
                                // xxx: if(){ xxx }
                                parseStatement(statement);
                                if (currentWord.getType() != TokenType.RIGHT_BRACE) {
                                    ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing '}'"));
                                } 
                                else {
                                    jmpInstructionPosition = generateInstruction(InstructionType.JMP, 0, -1);
                                    elseFinishPosition = jmpInstructionPosition; //如果没有else语句
                                    currentWord = lexer.nextToken();
                                    // Find the 'else'
                                    if (currentWord.getType() == TokenType.ELSE) {
                                        currentWord = lexer.nextToken();
                                        if (currentWord.getType() != TokenType.LEFT_BRACE) {
                                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing '{'"));
                                        }
                                        else {
                                            currentWord = lexer.nextToken();
                                            elseFinishPosition = parseStatement(statement); // else语句
                                            if (currentWord.getType() != TokenType.RIGHT_BRACE) {
                                                ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing '}'"));
                                            }
                                        }
                                        currentWord = lexer.nextToken();
                                    }
                                    Instruction jpcInstruction = generatedInstructionList.get(jpcInstructionPosition);
                                    jpcInstruction.setThird(jmpInstructionPosition+1);
                                    Instruction jmpInstruction = generatedInstructionList.get(jmpInstructionPosition);
                                    jmpInstruction.setThird(elseFinishPosition+1);
                                }
                            }
                        }
                    }
                    break;
                }
                // <loop-statement>
                case WHILE: {
                    // <loop-statement> ::= 'while' '(' <condition> ')' <statement>
                    int expInstructionPosition;//表达式指令开始的位置,jmp语句跳的地方
                    int jpcInstructionPosition;
                    int jmpInstructionPosition;

                    currentWord = lexer.nextToken();
                    // '('
                    if (currentWord.getType() != TokenType.LEFT_BRACKET ) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing '('"));
                    }
                    else {
                        expInstructionPosition = generatedInstructionList.size();
                        currentWord = lexer.nextToken();
                        // The statement inside while()
                        parseExpression(statement);
                        // ')'
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET ) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing ')'"));
                        } else {
                            //产生JPC指令 ，记录下来JPC指令的位置
                            jpcInstructionPosition = generateInstruction(InstructionType.JPC, 0, -1);
                            currentWord = lexer.nextToken();
                            if (currentWord.getType() != TokenType.LEFT_BRACE) {
                                ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing '{'"));
                            } else {
                                currentWord = lexer.nextToken();
                                parseStatement(statement); // while语句
                                if (currentWord.getType() != TokenType.RIGHT_BRACE) {
                                    ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing '}'"));
                                } else {
                                    jmpInstructionPosition = generateInstruction(InstructionType.JMP, 0, expInstructionPosition);
                                    //回填
                                    Instruction jpcInstruction = generatedInstructionList.get(jpcInstructionPosition);
                                    jpcInstruction.setThird(jmpInstructionPosition+1);
                                }
                                currentWord = lexer.nextToken();
                            }
                        }
                    }
                    break;
                }
                //函数调用、赋值语句
                case IDENTIFIER: {
                    String val = currentWord.getValue(); //保存原ident的值
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() == TokenType.LEFT_BRACKET) { //void函数调用
                        currentWord = lexer.nextToken();
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            //-1代表未知，如果调用函数正确，最后会查看函数定义表，回填函数入口地址。
                            lastInstructionPosition = generateInstruction(InstructionType.CAL, 0, -1);
                            FunctionCall functionCall = new FunctionCall(val, lastInstructionPosition,false, lexer.getLineNumber());
                            functionCallList.add(functionCall);
                        }
                        currentWord = lexer.nextToken(); //表达式处理的时候会含有此语句，所以不能放到外部，只能放到里面
                    } else if (currentWord.getType() == TokenType.EQUAL) { //赋值
                        //先判断该变量是否定义，在处理后面的表达式
                        Variable variable = variableMap.get(val);
                        if (variable != null && (variable.getScope().equals(statement)||variable.getScope().equals("global"))) {
                            currentWord = lexer.nextToken();
                            parseExpression(statement);
                            //赋值
                            if (variable.getScope().equals(statement)) {
                                lastInstructionPosition = generateInstruction(InstructionType.STO, 1,variable.getAddress()); //当前层
                            } else {
                                lastInstructionPosition = generateInstruction(InstructionType.STO, 0,variable.getAddress()); //全局变量，外层
                            }
                        } else {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,7, "变量未声明或不在作用域内"));
                        }
                    } else {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 8, "语句格式错误"));
                    }
                    if (currentWord.getType() != TokenType.SEMICOLON) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    currentWord = lexer.nextToken();
                    break;
                }
                //返回
                case RETURN: {
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.LEFT_BRACKET ) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing'('"));
                    } else {
                        currentWord = lexer.nextToken();
                        parseExpression(statement);
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            generateInstruction(InstructionType.STO, 1,0);
                            lastInstructionPosition = generateInstruction(InstructionType.RET, 0,1); //带return是带返回值的函数
                        }
                    }
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.SEMICOLON) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    currentWord = lexer.nextToken();
                    break;
                }
                // scan
                case SCAN: {
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.LEFT_BRACKET ) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing'('"));
                    } else {
                        currentWord = lexer.nextToken();
                        if (currentWord.getType() == TokenType.IDENTIFIER) {
                            // 查找要读的变量
                            Variable variable = variableMap.get(currentWord.getValue());
                            if (variable != null && (variable.getScope().equals(statement)||variable.getScope().equals("global"))) {
                                generateInstruction(InstructionType.RED,0,0); //生成 读值到栈顶 指令
                                //生成 储存到变量 指令
                                if (variable.getScope().equals(statement)) {
                                    lastInstructionPosition = generateInstruction(InstructionType.STO, 1,variable.getAddress()); //当前层
                                } else {
                                    lastInstructionPosition = generateInstruction(InstructionType.STO, 0,variable.getAddress()); //全局变量，外层
                                }
                            } else {
                                ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,7, "变量未声明或不在作用域内"));
                            }
                        } else {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,1,"Missing标识符(或标识符错误)"));
                        }
                        currentWord = lexer.nextToken();
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        }
                    }
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.SEMICOLON) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    currentWord = lexer.nextToken();
                    break;
                }
                    //  print statement
                    //  <print-statement> ::= 'print' '(' [<printable-list>] ')' ';'
                    //  <printable-list>  ::= <printable> {',' <printable>}
                    //  <printable> ::= <expression>
                case PRINT: {
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.LEFT_BRACKET ) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing'('"));
                    }
                    else {

                        currentWord = lexer.nextToken();
                        parseExpression(statement);
                        // TODO:Complete ',' in printable

                        if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        }
                        else {
                            lastInstructionPosition = generateInstruction(InstructionType.WRT,0,0); //生成 写 指令
                        }
                    }
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.SEMICOLON) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    currentWord = lexer.nextToken();
                    break;
                }
            }
        }
        return lastInstructionPosition;
    }

    //子程序处理
    private void parseSubProgram(String subProgram) throws Exception {
        int localCount = 0;
        int address;
        currentWord = lexer.nextToken();
        if (currentWord.getType() != TokenType.LEFT_BRACE) {
            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing'{'"));
        }
        else {
            currentWord = lexer.nextToken();
            //局部变量声明
            while (currentWord.getType() == TokenType.INT) {
                currentWord = lexer.nextToken();
                if (currentWord.getType() != IDENTIFIER) {
                    ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 1, "Missing标识符(或标识符错误)"));
                }
                else {
                    // 确保全局变量在主函数栈的相对位置
                    if (subProgram.equals("main")) {
                        address = globalCount + localCount + 3;
                    } else {
                        address = localCount + 3;
                    }
                    Variable variable = new Variable(currentWord.getValue(), subProgram, address);

                    //TODO 由于是用名字作为Key,所以变量不能重名
                    variableMap.put(currentWord.getValue(),variable);
                    localCount++;
                    currentWord = lexer.nextToken();

                    while (currentWord.getType() == TokenType.COMMA) {
                        currentWord = lexer.nextToken();
                        if (subProgram.equals("main")) {
                            address = globalCount + localCount + 3;
                        } else {
                            address = localCount + 3;
                        }
                        variable = new Variable(currentWord.getValue(), subProgram, address);
                        variableMap.put(currentWord.getValue(),variable);
                        localCount++;
                        currentWord = lexer.nextToken();
                    }
                    if (currentWord.getType() != TokenType.SEMICOLON) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                }
                currentWord = lexer.nextToken();
            }
            Function function = functionMap.get(subProgram);
            if (subProgram.equals("main")) {
                function.updateSize(localCount + globalCount);
            }
            else {
                function.updateSize(localCount);
            }
            int size = function.getSize();
            // 声明结束，为函数分配内存，记录当前函数入口地址
            int entryAddress = generateInstruction(InstructionType.INT, 0, size);
            function.setEntryAddress(entryAddress);
            // parse the statement
            parseStatement(subProgram);
            if (currentWord.getType() != TokenType.RIGHT_BRACE) {
                ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing'}'"));
            }
            else {
                //如果没有返回值的函数，末尾添加RET 0 0 指令(没有返回值的函数返回)
                if (!function.isHasReturn()) {
                    generateInstruction(InstructionType.RET, 0,0);
                }
                currentWord = lexer.nextToken();
            }
        }
    }

    //回填函数调用语句
    private void rewriteFunctionCall() {
        for (FunctionCall functionCall : functionCallList) {
            Function function = functionMap.get(functionCall.getFunctionName());
            if (function != null) {
                // 调用函数的返回值是否正确
                boolean returnValueCorrect = (!functionCall.isWantReturnValue()) || (functionCall.isWantReturnValue() == function.isHasReturn() || function.isHasReturn());
                if (returnValueCorrect) {
                    //回填调用函数入口地址
                    Instruction callInstruction = generatedInstructionList.get(functionCall.getCallInstructionPosition());
                    callInstruction.setThird(function.getEntryAddress());
                } else {
                    ErrorMsgList.add(new ErrorMsg(functionCall.getCallSourcePosition(), 10, functionCall.getFunctionName()+" no return value"));
                }
            } else {
                ErrorMsgList.add(new ErrorMsg(functionCall.getCallSourcePosition(), 9, functionCall.getFunctionName()+" undefined function"));
            }
        }
    }

    private void rewriteMainPosition() {
        // Get main function from function map
        Function function = functionMap.get("main");
        if (function != null) {
            Instruction instruction = generatedInstructionList.get(1);
            instruction.setThird(function.getEntryAddress());
        } else {
            ErrorMsgList.add(new ErrorMsg(-1, 11, "Missing main() function"));
        }
    }

    // <C0-program> ::={<variable-declaration>}{<function-definition>}
    // TODO:處理main函數
    public void parseProgram() throws Exception {
        currentWord = lexer.nextToken();
        while (currentWord.getType() == TokenType.INT || currentWord.getType() == TokenType.VOID) {
            if (currentWord.getType() == TokenType.INT) {  //变量或者函数
                currentWord = lexer.nextToken();
                if (currentWord.getType() == IDENTIFIER) {
                    Variable variable;
                    String val = currentWord.getValue(); //保留该值
                    currentWord = lexer.nextToken();

                    if (currentWord.getType() == TokenType.COMMA) { //变量
                        //declareGlobalVariable(val,count); //val是变量名
                        //加入变量声明列表
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(val,variable);
                        globalCount++;
                        while (currentWord.getType() == TokenType.COMMA) {
                            currentWord = lexer.nextToken();
                            //declareGlobalVariable(currentWord.getValue(),count);
                            variable = new Variable(currentWord.getValue(), "global", globalCount + 3);
                            variableMap.put(currentWord.getValue(),variable);
                            globalCount++;
                            currentWord = lexer.nextToken();
                        }

                        if (currentWord.getType() == TokenType.SEMICOLON) { //分号  结束
                            currentWord = lexer.nextToken();
                        } else {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                        }
                    }
                    // TODO:Solve the parameters
                    else if (currentWord.getType() == TokenType.LEFT_BRACKET) {  //函数
                        currentWord = lexer.nextToken();
                        Function function = new Function(val, true);
                        functionMap.put(val,function);
                        if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                            ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            parseSubProgram(val);
                        }
                    } else if (currentWord.getType() == TokenType.SEMICOLON) { //仅有一个变量声明
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(val,variable);
                        globalCount++;
                        currentWord = lexer.nextToken();
                    } else {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                } else {
                    ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 1, "Error identifier"));
                }
            } else if (currentWord.getType() == TokenType.VOID) {
                currentWord = lexer.nextToken();
                String functionName = currentWord.getValue();
                Function function = new Function(functionName, false);
                functionMap.put(currentWord.getValue(),function);
                currentWord = lexer.nextToken();
                if (currentWord.getType() != TokenType.LEFT_BRACKET) {
                    ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 4, "Missing'('"));
                } else {
                    currentWord = lexer.nextToken();
                    if (currentWord.getType() != TokenType.RIGHT_BRACKET) {
                        ErrorMsgList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                    } else {
                        parseSubProgram(functionName);
                    }
                }
            }
        }
        //回填函数调用入口地址
        rewriteFunctionCall();
        //回填main函数入口地址
        rewriteMainPosition();
    }

}