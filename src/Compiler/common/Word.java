package Compiler.common;

/**
 * Title：Find the every word and get its type
 * Description：analyse it
 * Created by Adam
 */
public class Word {
    private Symbol type;

    //如果是标识符，存入标识符的名字,如果是数字,存入数字的值（注意转化成int）,如果是char也转化成数字
    private String value = " ";

    public Symbol getType() {
        return type;
    }

    public void setType(Symbol type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Word{" + "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
