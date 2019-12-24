package Symbol;

import Common.TokenType;

import java.util.ArrayList;

public class Function {
    public String name;
    public int parametersNum;
    public TokenType returnType;
    public ArrayList<Command>commandArrayList;
    public int index;

    public Function() {

    }

    public Function(String name, int parametersNum, TokenType returnType, int index) {
        this.name = name;
        this.parametersNum = parametersNum;
        this.returnType = returnType;
        this.index = index;
        commandArrayList=new ArrayList<Command>();
    }
}
