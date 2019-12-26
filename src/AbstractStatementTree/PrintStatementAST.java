package AbstractStatementTree;

import Symbol.Command;

import java.util.ArrayList;

public class PrintStatementAST extends AbstractSyntaxTree {
    private ArrayList<ExpressionAST>printExpr;
    public void add(ExpressionAST p){
        printExpr.add(p);
    }
    public PrintStatementAST(){
        printExpr=new ArrayList<>();
    }

    @Override
    public void generate() {
        for (int i = 0; i < printExpr.size(); ++i) {
            printExpr.get(i).generate();
            if (i == printExpr.size() - 1)
                symbol.addCommand(currentFunc, new Command("iprint", -1, -1));
            else {
                symbol.addCommand(currentFunc, new Command("iprint", -1, -1));
                symbol.addCommand(currentFunc, new Command("bipush", 32, -1));
                symbol.addCommand(currentFunc, new Command("cprint", -1, -1));
            }
        }
        symbol.addCommand(currentFunc, new Command("printl", -1, -1));
    }
}
