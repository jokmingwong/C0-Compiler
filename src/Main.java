import Common.*;
import Compiler.*;
import Symbol.*;

import java.io.FileReader;
import java.io.FileWriter;

import static java.awt.PageAttributes.MediaType.C0;

public class Main{
    private static void print(Object o){
        System.out.println(o);
    }
    private static void println(Object o){
        System.out.println(o);
    }
    private static void output(FileReader input, FileWriter output, OutputType type) {
        Symbol symbol = new Parser(new Lexer(input).allTokens()).parse()._generate();
        switch (type) {
            case ASM: {
                symbol.outputAssemble(output);
                break;
            }
            /*
            case BIN: {
                symbol.outputBinary(output);
                break;
            }
            */
            default:
                System.out.println("Output Error");
                System.exit(-1);
        }
    }
    public static void main(String[] args) {
        println("This is a test:");
        try {
            FileReader fr=new FileReader("src\\test.c0");
            FileWriter wr=new FileWriter("src\\output.s0");
            output(fr,wr,OutputType.ASM);
            wr.flush();
            wr.close();
        }catch (Exception e){
            e.printStackTrace();
            //ErrorMsg.Error("The input file not exist");
        }
    }
}