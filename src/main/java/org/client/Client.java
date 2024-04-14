package org.client;

import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.*;
import org.security.SHA256;
import org.security.User;
import org.sqlite.SQLiteConnection;

import java.io.CharConversionException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Objects;

public class Client implements MsgReadListener {
    private String message = null;
    private final SocketConnection socketConnection;
    private final ClientConnectionHandler clientConnectionHandler;
    private String username;
    public Client() throws IOException, SQLException {
        this.socketConnection = new SocketConnection("localhost",SocketConnection.SERVER_PORT);
        clientConnectionHandler = new ClientConnectionHandler(this);
        socketConnection.addMsgReadListener(clientConnectionHandler);
        socketConnection.addMsgReadListener(this);
        socketConnection.startReading();

        MessageDB messageDB = new MessageDB();
        messageDB.connect();
        if(!messageDB.containsChatsTable()){
            messageDB.createChatsTable();
        }
        messageDB.close();
    }

    public void login(String username, String password) throws IOException, ChatProtocolException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.login(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    public void signup(String username, String password) throws IOException, ChatProtocolException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.signup(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    public void signupWithoutEncryption(String username, String password) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.signup(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    public void loginWithoutEncryption(String username,String password) throws ChatProtocolException, IOException {
        socketConnection.writeInstruction(InstructionBuilder.login(username,password));

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    public boolean invokeDone() throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.invokeDone());

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("DONE")){
            return true;
        } else if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        } else{
            return false;
        }
    }

    public String invokeOutput(String message) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.invokeOutput(message));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("OUTPUT")){
            return instruction.getParam("message");
        } else if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        } else {
            throw new ChatProtocolException("Unknown error");
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

    public void sendMessage(String message, String username) throws IOException, ChatProtocolException, SQLException {
        socketConnection.writeInstruction(InstructionBuilder.sendMessage(message, username));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        MessageDB messageDB = new MessageDB();
        messageDB.connect();
        if(!messageDB.containsChat(username)){
            messageDB.createChat(username);
        }

        Message msg = new Message(this.username,message);

        messageDB.addMessage(msg,username);

        messageDB.close();
    }

    public String waitForMessage(){
        message = null;

        while(Objects.equals(null,message)){
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

    public void close() throws IOException, SQLException {
        socketConnection.close();
    }
}
