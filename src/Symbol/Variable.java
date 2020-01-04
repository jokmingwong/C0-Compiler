package Symbol;

public class Variable {
    String name;
    boolean isConst;
    boolean isInit;
    public int index;
    String scope;

    Variable(String _name, Boolean _isConst, Boolean _isInit, int _index, String _scope){
        name=_name;
        isConst=_isConst;
        isInit=_isInit;
        index=_index;
        scope=_scope;
    }


}
