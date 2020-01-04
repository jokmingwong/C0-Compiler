package AbstractSyntaxTree;

import Common.TokenType;
import Symbol.Command;

import java.util.ArrayList;

public class MultiplicativeExpressionAST extends AbstractSyntaxTree{
    private ArrayList<UnaryExpressionAST>unaryExpr;
    private ArrayList<TokenType>op;

    public MultiplicativeExpressionAST(){
        unaryExpr=new ArrayList<>();
        op=new ArrayList<>();
    }

    public void add(UnaryExpressionAST p){
        unaryExpr.add(p);
    }

    public void add(TokenType type){
        op.add(type);
    }

    @Override
    public void generate(){
        if (unaryExpr.size() == 1) unaryExpr.get(0).generate();
        else {
            int index = 2;
            unaryExpr.get(0).generate();
            unaryExpr.get(1).generate();
            symbol.addCommand(currentFunc, new Command(op.get(0) == TokenType.MULTIPLY? "imul" : "idiv", -1, -1));
            while (index < unaryExpr.size()) {
                unaryExpr.get(index).generate();
                symbol.addCommand(currentFunc, new Command(op.get(index - 1)== TokenType.MULTIPLY ? "imul" : "idiv", -1, -1));
                index++;
            }
        }
    }


}
