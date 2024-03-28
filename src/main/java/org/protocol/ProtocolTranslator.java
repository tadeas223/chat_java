package org.protocol;

public class ProtocolTranslator {
    public static String encode(Instruction instruction){
        StringBuilder msg = new StringBuilder(instruction.getName());

        msg.append("{");

        if(instruction.getParamList() != null){
            for(String key : instruction.getParamList().keySet()){
                msg.append(key + "=" + instruction.getParamList().get(key) + ";");
            }
        }

        msg.append("}");

        if(!checkValidity(msg.toString())){
            throw new RuntimeException("Message encoding ended up in a invalid string");
        }
        return msg.toString();
    }

    public static Instruction decode(String message) throws InvalidStringException {
        if(!checkValidity(message)){
            throw new InvalidStringException("This is not a valid protocol message");
        }

        if(!message.contains("{")){
            return new Instruction(message);
        }

        int bracketIndex = message.indexOf('{');
        
        String name = message.substring(0,bracketIndex);
        ParamList paramList = new ParamList();

        if(message.contains(";")){
            String params = message.substring(bracketIndex+1,message.length()-1);

            String[] choppedParams = params.split(";");

            try{
                for(String param : choppedParams){
                    String[] kvp = param.split("=");
                    paramList.put(kvp[0],kvp[1]);
                }
            } catch (ArrayIndexOutOfBoundsException e){
                throw new InvalidStringException("Invalid instruction");
            }

        }

        return new Instruction(name, paramList);
    }

    public static boolean checkValidity(String message){
        return message.matches(".+([{](.+=.+;)*[}])?");
    }
}
