package test;

import Compiler.common.ErrorMsg;
import Compiler.Instruction.Instruction;
import Compiler.parser.Function;
import Compiler.parser.FunctionCall;
import Compiler.parser.ProgramParser;
import Compiler.parser.Variable;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Title：单元测试程序
 * Description：
 * Created by Myth on 5/11/2017.
 */
public class ProgramParserTest {
    @Test
    public void parseProgram() throws Exception {
        ProgramParser programParser = new ProgramParser("src/test/source/factorial.txt");
        programParser.parseProgram();
        List<ErrorMsg> wrongList = programParser.getWrongList();
        Map<String,Variable> variableMap = programParser.getVariableMap();
        Map<String,Function> functionMap = programParser.getFunctionMap();
        List<Instruction> instructionList = programParser.getGeneratedInstructionList();
        List<FunctionCall> functionCallList = programParser.getFunctionCallList();
        List<String> sourceList = programParser.getSourceCodeLineList();
        for (ErrorMsg wrong : wrongList) {
            System.out.println(wrong);
        }

        for (String s : sourceList) {
            System.out.println(s);
        }

        for (Map.Entry<String,Variable> entry : variableMap.entrySet()) {
            System.out.println(entry.getValue());
        }
        for (Map.Entry<String,Function> entry : functionMap.entrySet()) {
            System.out.println(entry.getValue());
        }
        for (FunctionCall functionCall : functionCallList) {
            System.out.println(functionCall);
        }

        int i = 0;
        for (Instruction instruction : instructionList) {
            //System.out.println(i + ": " +instruction.toString());
            System.out.println(instruction.toString());
            i++;
        }

    }

}