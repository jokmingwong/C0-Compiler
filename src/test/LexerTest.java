package test;

import Compiler.common.TokenType;
import Compiler.lexer.Lexer;
import org.junit.Test;

import java.util.List;


public class LexerTest {

    @Test
    public void openFile() throws Exception {
        Lexer lexer = new Lexer();
        lexer.analyseFile( "src/test/source/factorial.txt");
        List<String> list = lexer.getSourceCodeLineList();
        for (String s: list) {
            System.out.println(s);
        }
    }

    @Test
    public void getSymbol() throws Exception {
        Lexer lexer = new Lexer();
        lexer.analyseFile("src/test/source/factorial.txt");
        Word word = new Word();
        while (word.getType()!= TokenType.END) {
            word = lexer.nextToken();
            System.out.println(word);
        }
    }

}
