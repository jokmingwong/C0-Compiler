package Compiler.common;

public class ErrorMsg {
    private int ErrorCode;
    private String information;
    private  int linePosition;

    public static void Error(String information) {
        System.out.println(information);
        System.exit(0);
    }

}
