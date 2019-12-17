package Compiler.parser;

import Compiler.common.Symbol;
import Compiler.common.Word;
import Compiler.common.ErrorMsg;
import Compiler.Instruction.Instruction;
import Compiler.Instruction.InstructionType;
import Compiler.lexer.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Compiler.common.Symbol.IDENTIFIER;

/**
 * Title：程序处理
 * Description： <程序>->[<全局变量定义部分>] {<自定义函数定义部分>} <主函数>
 * Warning: 注意生成的指令是从位置1开始的
 * Created by Myth on 5/4/2017.
 */
public class ProgramParser {
    private Lexer lexer;
    private Word word; //每次去到的单词
    private List<ErrorMsg> wrongList;
    private List<FunctionCall> functionCallList; //记录函数调用
    //为方便定位变量与函数(名字作为Key),所以一定要注意函数、变量（也包括不同作用域）不能重名,使用Map表示 变量表与函数表
    private Map<String, Variable> variableMap;
    private Map<String, Function> functionMap;
    //TODO 考虑是否在开始加一条跳转到主函数指令
    /**
     * 生成的指令，指令从位置1开始
     */
    private List<Instruction> generatedInstructionList;
    private int globalCount = 0;
    // 用来判断是否属于某一语法单位
    private boolean[] statementBeginSymbolFlags;  // 语句开始符号集
    private boolean[] factorBeginSymbolFlags; //因子开始符号集

    public List<ErrorMsg> getWrongList() {
        return wrongList;
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

    public List<String> getSourceCodeLineList() {
        return lexer.getSourceCodeLineList();
    }

    public ProgramParser(String pathname) throws Exception {
        //相关初始化
        lexer = new Lexer();
        wrongList = new ArrayList<>();
        functionCallList = new ArrayList<>();
        variableMap = new HashMap<>();
        functionMap = new HashMap<>();
        generatedInstructionList = new ArrayList<>();
        //为了使产生的代码从下标1开始，添加一个占位元素占据下标0位置
        generatedInstructionList.add(new Instruction());
        generatedInstructionList.add(new Instruction(InstructionType.JMP, 0, -1)); //产生一个跳转到主函数的指令，最后回填main位置
        lexer.openFile(pathname);
        statementBeginSymbolFlags = new boolean[24];  // 语句开始符号集
        factorBeginSymbolFlags = new boolean[24];  //因子开始符号集

        statementBeginSymbolFlags[Symbol.IDENTIFIER.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.IF.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.WHILE.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.IDENTIFIER.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.RETURN.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.SCAN.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.PRINT.ordinal()]= true;

        factorBeginSymbolFlags[Symbol.IDENTIFIER.ordinal()] = true;
        factorBeginSymbolFlags[Symbol.NUMBER.ordinal()] = true;
        factorBeginSymbolFlags[Symbol.LEFT_BRACKET.ordinal()] = true;

    }

    //生成指令,返回新指令所在的位置
    private int generateInstruction(InstructionType type, int layer, int third) {
        Instruction instruction = new Instruction();
        instruction.setName(type);
        instruction.setLayer(layer);
        instruction.setThird(third);
        generatedInstructionList.add(instruction);
        return generatedInstructionList.indexOf(instruction);
    }

    // 因子处理
    private void parseFactor(String functionName) throws Exception {
        while (factorBeginSymbolFlags[word.getType().ordinal()]) // 循环直到不是因子开始符号
        {
            switch (word.getType()) {
                case IDENTIFIER:{   //因子为变量或者函数调用
                    String val = word.getValue(); //保存值，因为不知道是变量还是函数
                    word = lexer.getToken();
                    if (word.getType() == Symbol.LEFT_BRACKET) { //函数调用
                        word = lexer.getToken();
                        if (word.getType() != Symbol.RIGHT_BRACKET) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            int position = generateInstruction(InstructionType.CAL, 0, -1);
                            FunctionCall functionCall = new FunctionCall(val, position,true, lexer.getLineNumber()+1);
                            functionCallList.add(functionCall);
                        }
                        word = lexer.getToken();
                    } else { //变量
                        Variable variable = variableMap.get(val);
                        if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                            if (variable.getScope().equals(functionName)) {
                                generateInstruction(InstructionType.LOD, 1,variable.getAddress()); //当前层
                            } else {
                                generateInstruction(InstructionType.LOD, 0,variable.getAddress()); //全局变量，外层
                            }
                        } else {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 7, "变量未声明或不在作用域内"));
                        }
                    }
                    break;
                }
                case NUMBER: {   //因子为数
                    generateInstruction(InstructionType.LIT, 0, Integer.parseInt(word.getValue()));
                    word = lexer.getToken();
                    break;
                }
                case LEFT_BRACKET: {    //因子为表达式
                    word = lexer.getToken();
                    parseExpression(functionName);
                    if (word.getType() != Symbol.RIGHT_BRACKET) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                    }
                    word = lexer.getToken();
                    break;
                }
            }
        }
    }

    // 项处理 <因子>｛(*｜/) <因子>｝
    private void parseTerm(String functionName) throws Exception {
        Symbol symbol;
        parseFactor(functionName);	// 处理因子
        while (word.getType() == Symbol.MULTIPLY || word.getType() == Symbol.DIVIDE) {
            symbol = word.getType();
            word = lexer.getToken();
            parseFactor(functionName);
            if (symbol == Symbol.MULTIPLY) { // 生成乘法指令
                generateInstruction(InstructionType.MUL, 0, 0);
            } else { // 生成除法指令
                generateInstruction(InstructionType.DIV, 0, 0);
            }
        }
    }

    //表达式处理  [+｜-] <项> { (+｜-) <项>}
    // TODO 第一个项 不支持 + - ：<项> { (+｜-) <项>}
    private void parseExpression(String functionName) throws Exception {
        Symbol symbol;
        parseTerm(functionName); //处理项
        while (word.getType() ==  Symbol.PLUS || word.getType() == Symbol.MINUS) {
            symbol = word.getType();
            word = lexer.getToken();
            parseTerm(functionName);
            if (symbol == Symbol.PLUS)
            {
                generateInstruction(InstructionType.ADD, 0, 0); // 生成加法指令
            } else {
                generateInstruction(InstructionType.SUB, 0, 0); // 生成减法指令
            }
        }
    }

    /*处理语句
    <语句>-> <条件语句>｜<循环语句> | '{'<语句序列>'}' | <自定义函数调用语句> | <赋值语句> | <返回语句> | <读语句> | <写语句> | ;
    返回 最后一次生成的指令地址（在generatedInstructionList中的位置）
    */
    private int parseStatement(String functionName) throws Exception {
        int lastInstructionPosition = -1;
        while(statementBeginSymbolFlags[word.getType().ordinal()]) {  //循环直到不是语句开始符号
            switch (word.getType()) {
                //<condition-statement> :: 'if' '(' <condition> ')' <statement> ['else' <statement>]
                case IF: {
                    int jpcInstructionPosition;
                    int jmpInstructionPosition;
                    int elseFinishPosition;
                    // Judge the operator '('
                    word = lexer.getToken();
                    if (word.getType() != Symbol.LEFT_BRACKET ) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing '('"));
                    } 
                    else {
                        word = lexer.getToken();
                        parseExpression(functionName); // if判别表达式
                        if (word.getType() != Symbol.RIGHT_BRACKET ) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing ')'"));
                        } 
                        else {
                            //生成JPC指令，记录下来此条指令的地址(用于回填)
                            jpcInstructionPosition = generateInstruction(InstructionType.JPC, 0, -1);
                            word = lexer.getToken();
                            if (word.getType() != Symbol.LEFT_BRACE) {
                                wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing'{'"));
                            } 
                            else {
                                word = lexer.getToken();
                                parseStatement(functionName); // if语句
                                if (word.getType() != Symbol.RIGHT_BRACE) {
                                    wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing '}'"));
                                } 
                                else {
                                    jmpInstructionPosition = generateInstruction(InstructionType.JMP, 0, -1);
                                    elseFinishPosition = jmpInstructionPosition; //如果没有else语句
                                    word = lexer.getToken();
                                    if (word.getType() == Symbol.ELSE) {
                                        word = lexer.getToken();
                                        if (word.getType() != Symbol.LEFT_BRACE) {
                                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing '{'"));
                                        } 
                                        else {
                                            word = lexer.getToken();
                                            elseFinishPosition = parseStatement(functionName); // else语句
                                            if (word.getType() != Symbol.RIGHT_BRACE) {
                                                wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing '}'"));
                                            }
                                        }
                                        word = lexer.getToken();
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
                //循环语句
                case WHILE: {
                    int expInstructionPosition;//表达式指令开始的位置,jmp语句跳的地方
                    int jpcInstructionPosition;
                    int jmpInstructionPosition;
                    word = lexer.getToken();
                    if (word.getType() != Symbol.LEFT_BRACKET ) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing '('"));
                    } else {
                        expInstructionPosition = generatedInstructionList.size();
                        word = lexer.getToken();
                        parseExpression(functionName); //while表达式
                        if (word.getType() != Symbol.RIGHT_BRACKET ) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing ')'"));
                        } else {
                            //产生JPC指令 ，记录下来JPC指令的位置
                            jpcInstructionPosition = generateInstruction(InstructionType.JPC, 0, -1);
                            word = lexer.getToken();
                            if (word.getType() != Symbol.LEFT_BRACE) {
                                wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing '{'"));
                            } else {
                                word = lexer.getToken();
                                parseStatement(functionName); // while语句
                                if (word.getType() != Symbol.RIGHT_BRACE) {
                                    wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing '}'"));
                                } else {
                                    jmpInstructionPosition = generateInstruction(InstructionType.JMP, 0, expInstructionPosition);
                                    //回填
                                    Instruction jpcInstruction = generatedInstructionList.get(jpcInstructionPosition);
                                    jpcInstruction.setThird(jmpInstructionPosition+1);
                                }
                                word = lexer.getToken();
                            }
                        }
                    }
                    break;
                }
                //函数调用、赋值语句
                case IDENTIFIER: {
                    String val = word.getValue(); //保存原ident的值
                    word = lexer.getToken();
                    if (word.getType() == Symbol.LEFT_BRACKET) { //void函数调用
                        word = lexer.getToken();
                        if (word.getType() != Symbol.RIGHT_BRACKET) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            //-1代表未知，如果调用函数正确，最后会查看函数定义表，回填函数入口地址。
                            lastInstructionPosition = generateInstruction(InstructionType.CAL, 0, -1);
                            FunctionCall functionCall = new FunctionCall(val, lastInstructionPosition,false, lexer.getLineNumber());
                            functionCallList.add(functionCall);
                        }
                        word = lexer.getToken(); //表达式处理的时候会含有此语句，所以不能放到外部，只能放到里面
                    } else if (word.getType() == Symbol.EQUAL) { //赋值
                        //先判断该变量是否定义，在处理后面的表达式
                        Variable variable = variableMap.get(val);
                        if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                            word = lexer.getToken();
                            parseExpression(functionName);
                            //赋值
                            if (variable.getScope().equals(functionName)) {
                                lastInstructionPosition = generateInstruction(InstructionType.STO, 1,variable.getAddress()); //当前层
                            } else {
                                lastInstructionPosition = generateInstruction(InstructionType.STO, 0,variable.getAddress()); //全局变量，外层
                            }
                        } else {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,7, "变量未声明或不在作用域内"));
                        }
                    } else {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 8, "语句格式错误"));
                    }
                    if (word.getType() != Symbol.SEMICOLON) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    word = lexer.getToken();
                    break;
                }
                //返回
                case RETURN: {
                    word = lexer.getToken();
                    if (word.getType() != Symbol.LEFT_BRACKET ) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing'('"));
                    } else {
                        word = lexer.getToken();
                        parseExpression(functionName);
                        if (word.getType() != Symbol.RIGHT_BRACKET) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            generateInstruction(InstructionType.STO, 1,0);
                            lastInstructionPosition = generateInstruction(InstructionType.RET, 0,1); //带return是带返回值的函数
                        }
                    }
                    word = lexer.getToken();
                    if (word.getType() != Symbol.SEMICOLON) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    word = lexer.getToken();
                    break;
                }
                //读
                case SCAN: {
                    word = lexer.getToken();
                    if (word.getType() != Symbol.LEFT_BRACKET ) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing'('"));
                    } else {
                        word = lexer.getToken();
                        if (word.getType() == Symbol.IDENTIFIER) {
                            // 查找要读的变量
                            Variable variable = variableMap.get(word.getValue());
                            if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                                generateInstruction(InstructionType.RED,0,0); //生成 读值到栈顶 指令
                                //生成 储存到变量 指令
                                if (variable.getScope().equals(functionName)) {
                                    lastInstructionPosition = generateInstruction(InstructionType.STO, 1,variable.getAddress()); //当前层
                                } else {
                                    lastInstructionPosition = generateInstruction(InstructionType.STO, 0,variable.getAddress()); //全局变量，外层
                                }
                            } else {
                                wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,7, "变量未声明或不在作用域内"));
                            }
                        } else {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,1,"Missing标识符(或标识符错误)"));
                        }
                        word = lexer.getToken();
                        if (word.getType() != Symbol.RIGHT_BRACKET) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        }
                    }
                    word = lexer.getToken();
                    if (word.getType() != Symbol.SEMICOLON) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    word = lexer.getToken();
                    break;
                }
                //写
                case PRINT: {
                    word = lexer.getToken();
                    if (word.getType() != Symbol.LEFT_BRACKET ) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1,4, "Missing'('"));
                    } else {
                        word = lexer.getToken();
                        parseExpression(functionName);
                        if (word.getType() != Symbol.RIGHT_BRACKET) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            lastInstructionPosition = generateInstruction(InstructionType.WRT,0,0); //生成 写 指令
                        }
                    }
                    word = lexer.getToken();
                    if (word.getType() != Symbol.SEMICOLON) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                    word = lexer.getToken();
                    break;
                }
            }
        }
        return lastInstructionPosition;
    }

    //子程序处理
    private void parseSubProgram(String functionName) throws Exception {
        int localCount = 0;
        int address;
        word = lexer.getToken();
        if (word.getType() != Symbol.LEFT_BRACE) {
            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 5, "Missing'{'"));
        } else {
            word = lexer.getToken();
            while (word.getType() == Symbol.INT) {  //局部变量声明
                word = lexer.getToken();
                if (word.getType() != IDENTIFIER) {
                    wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 1, "Missing标识符(或标识符错误)"));
                } else {
                    // 确保全局变量在主函数栈的相对位置
                    if (functionName.equals("main")) {
                        address = globalCount + localCount + 3;
                    } else {
                        address = localCount + 3;
                    }
                    Variable variable = new Variable(word.getValue(), functionName, address);
                    //TODO 由于是用名字作为Key,所以变量不能重名
                    variableMap.put(word.getValue(),variable);
                    localCount++;
                    word = lexer.getToken();
                    while (word.getType() == Symbol.COMMA) {
                        word = lexer.getToken();
                        if (functionName.equals("main")) {
                            address = globalCount + localCount + 3;
                        } else {
                            address = localCount + 3;
                        }
                        variable = new Variable(word.getValue(), functionName, address);
                        variableMap.put(word.getValue(),variable);
                        localCount++;
                        word = lexer.getToken();
                    }
                    if (word.getType() != Symbol.SEMICOLON) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                }
                word = lexer.getToken();
            }
            Function function = functionMap.get(functionName);
            if (functionName.equals("main")) {
                function.updateSize(localCount + globalCount);
            } else {
                function.updateSize(localCount);
            }
            int size = function.getSize();
            //声明结束，为函数分配内存，记录当前函数入口地址
            int entryAddress = generateInstruction(InstructionType.INT, 0, size);
            function.setEntryAddress(entryAddress);
            //语句
            parseStatement(functionName);
            if (word.getType() != Symbol.RIGHT_BRACE) {
                wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 6, "Missing'}'"));
            } else {
                //如果没有返回值的函数，末尾添加RET 0 0 指令(没有返回值的函数返回)
                if (!function.isHasReturn()) {
                    generateInstruction(InstructionType.RET, 0,0);
                }
                word = lexer.getToken();
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
                    wrongList.add(new ErrorMsg(functionCall.getCallSourcePosition(), 10, functionCall.getFunctionName()+" 无返回值"));
                }
            } else {
                wrongList.add(new ErrorMsg(functionCall.getCallSourcePosition(), 9, functionCall.getFunctionName()+" 函数未定义"));
            }
        }
    }

    private void rewriteMainPosition() {
        Function function = functionMap.get("main");
        if (function != null) {
            Instruction instruction = generatedInstructionList.get(1);
            instruction.setThird(function.getEntryAddress());
        } else {
            wrongList.add(new ErrorMsg(-1, 11, "Missingmain函数"));
        }
    }

    //处理整个程序
    public void parseProgram() throws Exception {
        word = lexer.getToken();
        while (word.getType() == Symbol.INT || word.getType() == Symbol.VOID) {
            if (word.getType() == Symbol.INT) {  //变量或者函数
                word = lexer.getToken();
                Variable variable;
                if (word.getType() == IDENTIFIER) {
                    String val = word.getValue(); //保留该值
                    word = lexer.getToken();
                    if (word.getType() == Symbol.COMMA) { //变量
                        //declareGlobalVariable(val,count); //val是变量名
                        //加入变量声明列表
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(val,variable);
                        globalCount++;
                        while (word.getType() == Symbol.COMMA) {
                            word = lexer.getToken();
                            //declareGlobalVariable(word.getValue(),count);
                            variable = new Variable(word.getValue(), "global", globalCount + 3);
                            variableMap.put(word.getValue(),variable);
                            globalCount++;
                            word = lexer.getToken();
                        }
                        if (word.getType() == Symbol.SEMICOLON) { //分号  结束
                            word = lexer.getToken();
                        } else {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                        }
                    } else if (word.getType() == Symbol.LEFT_BRACKET) {  //函数
                        word = lexer.getToken();
                        Function function = new Function(val, true);
                        functionMap.put(val,function);
                        if (word.getType() != Symbol.RIGHT_BRACKET) {
                            wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
                        } else {
                            parseSubProgram(val);
                        }
                    } else if (word.getType() == Symbol.SEMICOLON) { //仅有一个变量声明
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(val,variable);
                        globalCount++;
                        word = lexer.getToken();
                    } else {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 2, "Missing';'"));
                    }
                } else {
                    wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 1, "Missing标识符(或标识符错误)"));
                }
            } else if (word.getType() == Symbol.VOID) {
                word = lexer.getToken();
                String functionName = word.getValue();
                Function function = new Function(functionName, false);
                functionMap.put(word.getValue(),function);
                word = lexer.getToken();
                if (word.getType() != Symbol.LEFT_BRACKET) {
                    wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 4, "Missing'('"));
                } else {
                    word = lexer.getToken();
                    if (word.getType() != Symbol.RIGHT_BRACKET) {
                        wrongList.add(new ErrorMsg(lexer.getLineNumber()+1, 3, "Missing')'"));
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