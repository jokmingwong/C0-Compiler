import Common.ErrorMsg;
import Common.Token;
import Compiler.Lexer;

import java.io.FileReader;

public class Main{
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        try {
            // String dic=System.getProperty("user.dir");
            FileReader fr = new FileReader("src\\test.c0");
            for(Token t:lexer.allTokens(fr)){
                System.out.println(t);
            }
        }catch (Exception e){
            ErrorMsg.Error("The file does not exist");
        }

    }
}