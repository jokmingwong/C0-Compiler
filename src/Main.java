import Compiler.common.Symbol;
import Compiler.common.Word;
import Compiler.lexer.Lexer;


public class Main {

    public static void main(String[] args) {
        // Test lexer
        Lexer lexer = new Lexer();
        try {
            lexer.openFile("src/test.c0");
        }catch (Exception e){
            e.printStackTrace();
        }

        Word word = new Word();
        while (word.getType()!= Symbol.END) {
            try {
                word = lexer.getToken();
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println(word);
        }


    }
}
