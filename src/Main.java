import Common.OutputType;
import Compiler.*;
import Symbol.*;

import argparser.*;
import java.io.FileReader;
import java.io.FileWriter;


public class Main{
    private static void println(Object o){
        System.out.println(o);
    }

    private static void output (String inputFileName, String outputFileName, OutputType type) {
        try {
            FileReader input = new FileReader(inputFileName);
            FileWriter output = new FileWriter(outputFileName);
            Symbol symbol = new Parser(new Lexer(input).allTokens()).parse()._generate();
            switch (type) {
                case ASSEMBLY: {
                    symbol.outputAssemble(output);
                    break;
                }
                case BINARY: {
                    symbol.outputBinary(outputFileName);
                    break;
                }

                default:
                    println("Output Error");
                    System.exit(-1);
            }
            output.flush();
            output.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        ArgParser program=new ArgParser("CO Compiler");
        StringHolder outputFileNameStringHolder=new StringHolder();
        StringHolder inputFileNameStringHolder=new StringHolder();
        program.addOption("-c %s",inputFileNameStringHolder);
        program.addOption("-s %s",inputFileNameStringHolder);
        program.addOption("-o %s",outputFileNameStringHolder);
        program.matchAllArgs(args);

        String inputFileName="";
        String outputFileName="out.s0";

        if(inputFileNameStringHolder.value!=null){
            inputFileName=inputFileNameStringHolder.value;
            if(outputFileNameStringHolder.value!=null){
                outputFileName=outputFileNameStringHolder.value;
            }
        }else{
            println("Help");
        }
        // Todo: How to divide two different command -c or -s?
        output(inputFileName,outputFileName,OutputType.ASSEMBLY);
        //ErrorMsg.Error("The input file not exist");
        println("Compile finished");
    }
}