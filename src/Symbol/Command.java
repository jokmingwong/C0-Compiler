package Symbol;

public class Command {
    int arg1,arg2,index;
    String command;
    public Command(){}
    public Command(String c,int a1,int a2){
        command=c;
        arg1=a1;
        arg2=a2;
    }

    void setIndex(int i){
        index=i;
    }
}
