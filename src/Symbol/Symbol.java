package Symbol;

import Common.ErrorMsg;
import Common.Pair;
import Common.TokenType;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Symbol {
    // Define 3 tables to store
    private ArrayList<Variable> variableTable=new ArrayList<>();
    private ArrayList<Function> functionTable=new ArrayList<>();
    private ArrayList<Command> startCode=new ArrayList<>();

    private static void print(Object o) {
        System.out.print(o);
    }

    public int globalIndex;
    public int localIndex;

    Pair<Integer, Pair<Integer, Integer>> getCommandHex(String s) {
        switch (s) {
            case "bipush":
                return new Pair<>(0x01, new Pair<>(1, 0));
            case "ipush":
                return new Pair<>(0x02, new Pair<>(4, 0));
            case "loada":
                return new Pair<>(0x0a, new Pair<>(2, 4));
            case "snew":
                return new Pair<>(0x0c, new Pair<>(4, 0));
            case "iload":
                return new Pair<>(0x10, new Pair<>(0, 0));
            case "istore":
                return new Pair<>(0x20, new Pair<>(0, 0));
            case "iadd":
                return new Pair<>(0x30, new Pair<>(0, 0));
            case "isub":
                return new Pair<>(0x34, new Pair<>(0, 0));
            case "imul":
                return new Pair<>(0x38, new Pair<>(0, 0));
            case "idiv":
                return new Pair<>(0x3c, new Pair<>(0, 0));
            case "ineg":
                return new Pair<>(0x40, new Pair<>(0, 0));
            case "jmp":
                return new Pair<>(0x70, new Pair<>(2, 0));
            case "je":
                return new Pair<>(0x71, new Pair<>(2, 0));
            case "jne":
                return new Pair<>(0x72, new Pair<>(2, 0));
            case "jl":
                return new Pair<>(0x73, new Pair<>(2, 0));
            case "jge":
                return new Pair<>(0x74, new Pair<>(2, 0));
            case "jg":
                return new Pair<>(0x75, new Pair<>(2, 0));
            case "jle":
                return new Pair<>(0x76, new Pair<>(2, 0));
            case "call":
                return new Pair<>(0x80, new Pair<>(2, 0));
            case "ret":
                return new Pair<>(0x88, new Pair<>(0, 0));
            case "iret":
                return new Pair<>(0x89, new Pair<>(0, 0));
            case "iprint":
                return new Pair<>(0xa0, new Pair<>(0, 0));
            case "cprint":
                return new Pair<>(0xa2, new Pair<>(0, 0));
            case "printl":
                return new Pair<>(0xaf, new Pair<>(0, 0));
            case "iscan":
                return new Pair<>(0xb0, new Pair<>(0, 0));
        }
        return null;
    }

    public void outputAssemble(FileWriter output) {
        try {
            output.write(".constants:\n");
            for (Function f : functionTable) {
                output.write(f.index + " S " + '\"' + f.name + '\"' + '\n');
            }

            output.write(".start:\n");
            int index = 0;
            for (Command c : startCode) {
                index++;
                String iStr=""+index+"";
                output.write(iStr);
                output.write("\t");
                output.write(c.command);
                if (c.arg1 != -1) output.write(" " + c.arg1);
                if (c.arg2 != -1) output.write(", " + c.arg2);
                output.write("\n");
            }

            output.write(".functions:\n");
            for (Function f : functionTable) {
                output.write(f.index + " " + f.index + " ");
                output.write(f.parametersNum + " " + "1" + "\n");
            }
            for (Function f : functionTable) {
                output.write(".F" + f.index + ":\n");
                for (Command c : f.commandArrayList) {
                    output.write(c.index + "\t");
                    output.write(c.command);
                    if (c.arg1 != -1) output.write(" " + c.arg1);
                    if (c.arg2 != -1) output.write("," + c.arg2);
                    output.write("\n");
                }
            }
        }catch (Exception e){
            ErrorMsg.Error("Output assembly error");
        }
    }

    private ArrayList<Byte> int2bytes(int length , int target){
        ArrayList<Byte> bytes = new ArrayList<>();
        int start = 24;
        for(int i = 0 ; i < length; i++){
            bytes.add((byte) (( target >> ( start - i * 8 )) & 0xFF ));
        }
        return bytes;
    }

    public void getVariableTable() {
        print("Variable: \n");
        print("name        isInit        ");
        print("isConst        scope        ");
        print("index        \n");

        for (Variable v : variableTable) {
            print(v.name + "              " + v.isInit + "               ");
            print(v.isConst + "                " + (v.scope.equals("") ? "-" : v.scope));
            print("               ");
            print(v.index + '\n');
        }
        print('\n');
    }


    // Todo:How to solve binary file?
    public void outputBinary(String outputFileName) {
        ArrayList<Byte> output = new ArrayList<>();

        // Add magic number
        ArrayList<Byte> magic = int2bytes(4, 0x43303A29);
        output.addAll(magic);

        // Add version
        ArrayList<Byte> version = int2bytes(4, 0x01);
        output.addAll(version);

        // Add constant count
        ArrayList<Byte> constantsCount = int2bytes(2, functionTable.size());
        output.addAll(constantsCount);

        // Constant
        for(Function f:functionTable){
            output.addAll(int2bytes(1,0x00));
            output.addAll(int2bytes(2,f.name.length()));
            //output.addAll(int2bytes(f.name.length(),f.name.toCharArray()))
            ArrayList<Byte>valueByteArrayList=new ArrayList<>();
            byte[] valueBytes = f.name.getBytes(StandardCharsets.US_ASCII);
            for (Byte b : valueBytes){
                valueByteArrayList.add(b);
            }
            output.addAll(valueByteArrayList);
        }

        // Start code count
        output.addAll(int2bytes(2,startCode.size()));

        // Start code
        for(Command c:startCode){
            Pair<Integer, Pair<Integer, Integer>> command=getCommandHex(c.command);
            output.addAll(int2bytes(1, command.getFirst()));
            if(c.arg1!=-1){
                if(command.getSecond().getFirst()==1)
                    output.addAll(int2bytes(1,c.arg1));
                else if(command.getSecond().getFirst()==2){
                    output.addAll(int2bytes(2,c.arg1));
                }else if(command.getSecond().getFirst()==4){
                    output.addAll(int2bytes(4,c.arg2));
                }
            }
            if(c.arg2!=-1){
                output.addAll(int2bytes(4,c.arg2));
            }
        }

        // Function count
        output.addAll(int2bytes(2,functionTable.size()));

        // Function
        for(Function f:functionTable){
            output.addAll(int2bytes(2,f.index));
            output.addAll(int2bytes(2,f.parametersNum));
            output.addAll(int2bytes(2,1));
            output.addAll(int2bytes(2,f.commandArrayList.size()));

            // Command
            for(Command c:f.commandArrayList){
                Pair<Integer,Pair<Integer,Integer>>command=getCommandHex(c.command);
                output.addAll(int2bytes(1,command.getFirst()));
                if(c.arg1!=-1){
                    if(command.getSecond().getFirst()==1){
                        output.addAll(int2bytes(1,c.arg1));
                    }else if(command.getSecond().getFirst()==2){
                        output.addAll(int2bytes(2,c.arg1));
                    }else if(command.getSecond().getFirst()==4){
                        output.addAll(int2bytes(4,c.arg1));
                    }
                }
                if(c.arg2!=-1){
                    output.addAll(int2bytes(4,c.arg2));
                }
            }
        }
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFileName, true));
            byte[] temp=new byte[output.size()];
            for(int i=0;i<output.size();i++){
                temp[i]=output.get(i);
            }
            out.write(temp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getVariableIndex(String varName, String funcName) {
        for (Variable v : variableTable)
            if (v.name .equals(varName) && funcName.equals(v.scope))
                return v.index;
        return -1;
    }

    public int getFunctionIndex(String name) {
        for (Function f : functionTable)
            if (f.name.equals(name))
                return f.index;
        return -1;
    }

    public boolean isInitialized(String varName, String funcName) {
        for (Variable v: variableTable)
            if (v.name.equals(varName) && funcName.equals(v.scope))
                return v.isInit;
        return false;
    }

    public boolean isConst(String varName, String funcName) {
        for(Variable v:variableTable)
            if(v.name.equals(varName) && funcName.equals(v.scope))
                return v.isConst;
        return false;
    }

    public boolean isVoid(String name) {
        for (Function f: functionTable) if (f.name.equals(name)) return f.returnType == TokenType.VOID;
        return false;
    }

    public void addVariable(String name, boolean isConst, boolean isInit, String scope) {
        int index = scope .equals("")? (globalIndex++) : (localIndex++);
        variableTable.add(new Variable(name, isConst, isInit, index, scope));
    }

    public void addFunction(String name, int parametersNum, TokenType returnType) {
        int index = functionTable.size();
        functionTable.add(new Function(name, parametersNum, returnType,index));
    }

    public int addCommand(String name, Command c) {
        int index = -1;
        if (name.equals("")) {
            index = startCode.size();
            c.setIndex(index);
            startCode.add(c);
        } else {
            for (Function f : functionTable) {
                if (f.name.equals(name)) {
                    index = f.commandArrayList.size();
                    c.setIndex(index);
                    f.commandArrayList.add(c);
                }
            }
        }
        return index;
    }

    public void assignToVariable(String varName, String funcName) {
        for (Variable v :variableTable)
            if (v.name.equals(varName) && funcName.equals(v.scope))
                v.isInit = true;
    }

    public int getCommandLastNum(String name) {
        for (Function f : functionTable)
        if (f.name.equals(name))
            return f.commandArrayList.size() == 0 ? f.commandArrayList.size() : f.commandArrayList.size() - 1;
        return -1;
    }

    public void fillBack(String name, int index, int newX1, int newX2) {
        for (Function f : functionTable)
        if (f.name.equals(name)) {
            f.commandArrayList.get(index).arg1 = newX1;
            f.commandArrayList.get(index).arg2 = newX2;
        }
    }

}
