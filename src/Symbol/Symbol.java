package Symbol;

import Common.Pair;
import Common.TokenType;

import java.util.ArrayList;

public class Symbol {
    // Define 3 tables to store
    private ArrayList<Variable> variableTable;
    private ArrayList<Function> functionTable;
    private ArrayList<Command> startCode;

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

    public StringBuilder outputAssemble(StringBuilder output) {
        output.append(".constants:\n");
        for (Function f : functionTable) {
            output.append((f.index + ' ' + 'S' +' ' + '\"' +f.name +'\"' +'\n'));
        }

        output .append(".start:\n");
        int index = 0;
        for (Command c : startCode) {
            index++;
            output.append(index);
            output.append("\t");
            output.append(c.command);
            if (c.arg1 != -1) output.append(" "+c.arg1);
            if (c.arg2 != -1) output.append(", "+c.arg2);
            output.append("\n");
        }

        output.append(".functions:\n");
        for (Function f : functionTable) {
            output.append(f.index + ' ' +f.index + ' ');
            output.append(f.parametersNum + ' ' +1 +"\n");
        }
        for (Function f: functionTable) {
            output.append (".F" +f.index+":\n");
            for (Command c : f.commandArrayList) {
                output.append(c.index +"\t");
                output.append(c.command);
                if (c.arg1 != -1) output.append(" " + c.arg1);
                if (c.arg2 != -1) output.append("," + c.arg2);
                output.append("\n");
            }
        }
        return output;
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
    public StringBuilder outputBinary(StringBuilder output) {

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

    public void fillback(String name, int index, int newX1, int newX2) {
        for (Function f : functionTable)
        if (f.name.equals(name)) {
            f.commandArrayList.get(index).arg1 = newX1;
            f.commandArrayList.get(index).arg2 = newX2;
        }
    }

}
