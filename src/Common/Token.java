package Common;

public class Token {

    private TokenType _type;
    private Object _value;
    private Pair<Integer, Integer> _start_pos;
    private Pair<Integer, Integer> _end_pos;
    private Boolean _isEnd = false;

    public Token(TokenType type, Object value, int start_line, int start_column, int end_line,
          int end_column){
        _type=type;
        _value=value;
        _start_pos=new Pair<>(start_line,start_column);
        _end_pos=new Pair<>(end_line,end_column);
    }

    public Token(TokenType type, Object value, Pair<Integer,Integer>start, Pair<Integer, Integer> end)
    {
        this(type,value,start.getFirst(),start.getSecond(),end.getFirst(),end.getSecond());
    }

    public Boolean isEnd() {
        return _isEnd;
    }

    public Token(Token t) {
        _type = t._type;
        _value = t._value;
        _start_pos = t._start_pos;
        _end_pos = t._end_pos;
    }

    public Token(){
        _isEnd=true;
    }


    public String GetValueString() {
        return _value.toString();
    }

    public Pair<Integer, Integer> GetEndPos() {
        return _end_pos;
    }

    public Pair<Integer, Integer> GetStartPos() {
        return _start_pos;
    }

    public TokenType GetType(){
        return _type;
    }

    @Override
    public String toString() {
        return GetValueString();
    }
}
