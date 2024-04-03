package org.client;

import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.*;
import org.security.SHA256;

import java.io.IOException;

public class Client implements MsgReadListener {
    private String message = "";
    private final SocketConnection socketConnection;
    private final ClientConnectionHandler clientConnectionHandler;
    public Client() throws IOException {
        this.socketConnection = new SocketConnection("localhost",SocketConnection.SERVER_PORT);
        clientConnectionHandler = new ClientConnectionHandler(this);
        socketConnection.addMsgReadListener(clientConnectionHandler);
        socketConnection.addMsgReadListener(this);
        socketConnection.startReading();
    }

    public void login(String username, String password) throws IOException, ChatProtocolException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.login(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }
    }

    public void signup(String username, String password) throws IOException, ChatProtocolException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.signup(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }
    }

    public void signupWithoutEncryption(String username, String password) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.signup(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }
    }

    public void loginWithoutEncryption(String username,String password) throws ChatProtocolException, IOException {
        socketConnection.writeInstruction(InstructionBuilder.login(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }
    }

    public boolean isOnline(String username) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.isOnline(username));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("FALSE")){
            return false;
        } else if(instruction.getName().equals("TRUE")){
            return true;
        } else {
            throw new ChatProtocolException("Something went horribly wrong");
        }
    }

    public void sendMessage(String message, String username) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.sendMessage(message, username));

//        Instruction instruction = stringToInst(waitForMessage());
//
//        if(instruction.getName().equals("ERROR")){
//            throw new ChatProtocolException(instruction.getParam("message"));
//        }
    }
    public String waitForMessage(){
        String previousMessage = new String(message);

        while(previousMessage.equals(message)){
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // I don't care yet
            }
        }

        return message;
    }

    public Instruction stringToInst(String string) throws IOException {
        try{
            return ProtocolTranslator.decode(string);
        } catch (InvalidStringException e) {
            socketConnection.writeInstruction(InstructionBuilder.error("Invalid instruction"));
            return new Instruction("PlS DoN'T UsE tHiS eXaCt TeXt FoR InStRuCtIoN");
        }
    }

    @Override
    public void messageRead(String msg) {
        message = msg;
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }
}
