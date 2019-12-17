package Compiler.common;

public class ErrorMsg {
    private int ErrorCode;
    private String information;
    private  int linePosition;

    public ErrorMsg(int position,int number, String information) {
        this.ErrorCode = number;
        this.information = information;
        this.linePosition = position;
    }

    @Override
    public String toString() {
        return "Error in line"+linePosition+":"+ information;
    }
}
