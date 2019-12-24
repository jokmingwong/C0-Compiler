package Symbol;

public class Variable {
    public String name;
    public Boolean isConst;
    public Boolean isInit;
    public int index;  //偏移
    public String scope;     //""为全局


    public Variable() {}

    public Variable(String _name, Boolean _isConst, Boolean _isInit, int _index, String _scope){
        name=_name;
        isConst=_isConst;
        isInit=_isInit;
        index=_index;
        scope=_scope;
    }


}
