package Common;

public class Token {

    private TokenType type;
    private Object value;
    private Pair<Integer, Integer> startPos;
    private Pair<Integer, Integer> endPos;
    private Boolean _isEnd = false;

    private Token(TokenType type, Object value, int start_line, int start_column, int end_line,
          int end_column){
        this.type =type;
        this.value =value;
        startPos =new Pair<>(start_line,start_column);
        endPos =new Pair<>(end_line,end_column);
    }

    public Token(TokenType type, Object value, Pair<Integer,Integer>start, Pair<Integer, Integer> end)
    {
        this(type,value,start.getFirst(),start.getSecond(),end.getFirst(),end.getSecond());
    }

    public Boolean isEnd() {
        return _isEnd;
    }

    public Token(Token t) {
        type = t.type;
        value = t.value;
        startPos = t.startPos;
        endPos = t.endPos;
    }

    public Token(){
        _isEnd=true;
    }


    public String GetValueString() {
        return value.toString();
    }

    public Pair<Integer, Integer> GetEndPos() {
        return endPos;
    }

    public Pair<Integer, Integer> GetStartPos() {
        return startPos;
    }

    public TokenType GetType(){
        return type;
    }

    @Override
    public String toString() {
        return GetValueString();
    }
}
