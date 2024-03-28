package org.protocol;

public class InstructionBuilder {

    public static Instruction login(String username,String password){
        ParamList params = new ParamList();
        params.put("username",username);
        params.put("password",password);
        return new Instruction("LOGIN",params);
    }

    public static Instruction signup(String username,String password){
        ParamList params = new ParamList();
        params.put("username",username);
        params.put("password",password);
        return new Instruction("SIGNUP",params);
    }

    public static Instruction error(String msg){
        return new Instruction("ERROR",new ParamList("msg",msg));
    }

    public static Instruction done() {
        return new Instruction("DONE");
    }

    public static Instruction output(String output){
        return new Instruction("OUTPUT",new ParamList("out",output));
    }

    public static Instruction getId(){
        return new Instruction("GET_ID");
    }

    public static Instruction logout(){
        return new Instruction("LOGOUT");
    }

    public static Instruction isOnline(String username){
        return new Instruction("IS_ONLINE", new ParamList("username",username));
    }

    public static Instruction trueInst(){
        return new Instruction("TRUE");
    }

    public static Instruction falseInst(){
        return new Instruction("FALSE");
    }

    public static Instruction message(String msg, String username){
        ParamList paramList = new ParamList();
        paramList.put("msg",msg);
        paramList.put("username", username);
        return new Instruction("MESSAGE",paramList);
    }

}
