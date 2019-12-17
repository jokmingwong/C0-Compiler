import Compiler.common.ErrorMsg;
import Compiler.Instruction.Instruction;
import Compiler.Instruction.Interpreter;
import Compiler.parser.Function;
import Compiler.parser.FunctionCall;
import Compiler.parser.ProgramParser;
import Compiler.parser.Variable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Title：主程序 —— 编译
 * Description：
 * Created by Myth on 6/1/2017.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入C0源程序文件名：");
        String sourceFileName = scanner.next();
        String currentPath = System.getProperty("user.dir"); // 获取当前路径
        ProgramParser programParser;
        try {
            //不能在IntelliJ上使用以下路径
            //programParser = new ProgramParser(currentPath + File.separator + sourceFileName);
            //IntelliJ IDEA路径
            programParser = new ProgramParser("src/" + sourceFileName);
        } catch (Exception e) {
            System.out.println("找不到 '"+ sourceFileName +"' 源文件");
            scanner.close();
            return;
        }
        try {
            programParser.parseProgram();
        } catch (Exception e) {
            System.out.println("编译错误！");
            scanner.close();
            return;
        }

        List<ErrorMsg> wrongList = programParser.getWrongList();

        //编译有错误时，打印错误
        if (wrongList.size() != 0) {
            System.out.println("源程序有错误：");
            for (ErrorMsg err : wrongList) {
                System.out.println(err);
            }
            return;
        }
        List<Instruction> instructionList = programParser.getGeneratedInstructionList();
        Map<String,Variable> variableMap = programParser.getVariableMap();
        Map<String,Function> functionMap = programParser.getFunctionMap();
        List<FunctionCall> functionCallList = programParser.getFunctionCallList();
        Interpreter interpreter = new Interpreter();

        interpreter.setInstructionList(instructionList);
        try {
            //将指令写入instruction.txt文件
            File instructionFile = new File(currentPath + File.separator + "instruction.txt");
            //File instructionFile = new File( "src/instruction.txt");
            interpreter.writeToFile(instructionFile);
        }  catch (IOException e){
            System.out.println("编译失败，输出指令至文件失败！");
            e.printStackTrace();
            return;
        }
        System.out.println("编译成功，已将生成指令写入instruction.txt");
        System.out.println("是否查看符号表 Y/N？");
        String choice;
        do {
            choice = scanner.next();
        } while (!"Y".equals(choice) && !"y".equals(choice) && !"N".equals(choice) && !"n".equals(choice));

        if("Y".equals(choice) || "y".equals(choice)) {
            System.out.println("变量定义表：");
            for (Map.Entry<String,Variable> entry : variableMap.entrySet()) {
                System.out.println(entry.getValue());
            }
            System.out.println("函数定义表：");
            for (Map.Entry<String,Function> entry : functionMap.entrySet()) {
                System.out.println(entry.getValue());
            }
            System.out.println("函数调用表：");
            for (FunctionCall functionCall : functionCallList) {
                System.out.println(functionCall);
            }
            System.out.println("");
        }

        scanner.close();
    }
}