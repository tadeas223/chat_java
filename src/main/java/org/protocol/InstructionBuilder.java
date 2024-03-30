package org.protocol;

/**
 * Builds a different kinds of instructions with given parameters.
 */
public class InstructionBuilder {
    public static Instruction helloWorld(){
        return new Instruction("HELLO_WORLD");
    }
    public static Instruction done(){return new Instruction("DONE");}
    public static Instruction output(String message){return new Instruction("OUTPUT",new ParamList("message",message));}
    public static Instruction error(String message){return new Instruction("ERROR",new ParamList("message",message));}
}
