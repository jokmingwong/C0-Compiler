package test;

import Compiler.common.Symbol;
import Compiler.common.Word;
import Compiler.lexer.Lexer;
import org.junit.Test;

import java.util.List;


public class LexerTest {

    @Test
    public void openFile() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile( "src/test/source/factorial.txt"); //注意路径
        List<String> list = lexer.getSourceCodeLineList();
        for (String s: list) {
            System.out.println(s);
        }
    }

    @Test
    public void getSymbol() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile("src/test/source/factorial.txt");
        Word word = new Word();
        while (word.getType()!= Symbol.END) {
            word = lexer.getToken();
            System.out.println(word);
        }
    }

}
