package Compiler.Instruction;

/**
 * Title：Target style
 * Description：
 * Created by Adam on 12/18/2019
 */
public class Instruction {
    private InstructionType name;  //指令名字
    private int layer; //层数（0全局变量、1当前层的变量）
    private int thirdNumber; //指令的第三个数

    public Instruction() {}

    public Instruction(InstructionType name, int layer, int third) {
        this.name = name;
        this.layer = layer;
        this.thirdNumber = third;
    }

    public InstructionType getName() {
        return name;
    }

    public void setName(InstructionType name) {
        this.name = name;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getThird() {
        return thirdNumber;
    }

    public void setThird(int third) {
        this.thirdNumber = third;
    }

    @Override
    public String toString() {
        return name + " " + layer + " " +thirdNumber;
    }
}
