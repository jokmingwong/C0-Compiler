package Symbol;

import Common.TokenType;

import java.util.ArrayList;

public class Function {
    String name;
    int parametersNum;
    TokenType returnType;
    ArrayList<Command>commandArrayList;
    public int index;

    Function(String name, int parametersNum, TokenType returnType, int index) {
        this.name = name;
        this.parametersNum = parametersNum;
        this.returnType = returnType;
        this.index = index;
        commandArrayList=new ArrayList<Command>();
    }
}
