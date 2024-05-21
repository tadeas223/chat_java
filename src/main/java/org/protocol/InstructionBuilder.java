package org.protocol;

import java.util.ArrayList;
import java.util.List;

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
    public static Instruction invokeDone(){
        return new Instruction("INVOKE_DONE");
    }

    public static Instruction invokeOutput(String message){
        return new Instruction("INVOKE_OUTPUT", new ParamList("message", message));
    }

    public static Instruction logout(){
        return new Instruction("LOGOUT");
    }

    public static Instruction login(String username, String password){
        ParamList paramList = new ParamList();

        paramList.put("username",username);
        paramList.put("password",password);

        return new Instruction("LOGIN", paramList);
    }

    public static Instruction sendMessage(String message, String username){
        ParamList paramList = new ParamList();

        paramList.put("username",username);
        paramList.put("message",message);

        return new Instruction("SEND_MESSAGE", paramList);
    }
    public static Instruction signup(String username, String password){
        ParamList paramList = new ParamList();

        paramList.put("username",username);
        paramList.put("password",password);

        return new Instruction("SIGN_UP", paramList);
    }

    public static Instruction isOnline(String username){
        return new Instruction("IS_ONLINE", new ParamList("username", username));
    }

    public static Instruction defaultInstruction(){
        return new Instruction("DEFAULT");
    }

    public static Instruction trueInstruction(){
        return new Instruction("TRUE");
    }

    public static Instruction falseInstruction(){
        return new Instruction("FALSE");
    }

    public static Instruction message(String message, String sender){
        ParamList paramList = new ParamList();

        paramList.put("message",message);
        paramList.put("sender",sender);

        return new Instruction("MESSAGE", paramList);
    }

    public static Instruction messageArray(int count){
        return new Instruction("ARRAY",new ParamList("count",String.valueOf(count)));
    }

    public static Instruction arrayEnd(){
        return new Instruction("ARRAY_END");
    }

    public static Instruction[] messageArrayWrap(Instruction[] arrayContent){
        ArrayList<Instruction> arrayList = new ArrayList<>(List.of(arrayContent));

        arrayList.add(0,messageArray(arrayContent.length));
        arrayList.add(arrayEnd());

        return arrayList.toArray(Instruction[]::new);
    }

    public static Instruction getId(){
        return new Instruction("GET_ID");
    }
}
