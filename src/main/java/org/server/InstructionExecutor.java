package org.server;

import org.connection.SocketConnection;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.security.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class is used for executing code for a specific instructions.
 */
public class InstructionExecutor {
    private final HashMap<String,Runnable> instMethodList = new HashMap<>();
    private Instruction currentInstruction = null;
    private ConnectionHandler currentHandler = null;
    private SocketConnection currentSocketConnection = null;
    private final SQLConnection sqlConnection = new SQLConnection();

    public InstructionExecutor(){
        try {
            sqlConnection.connect();
        } catch (SQLException e) {
            // IDK
            throw new RuntimeException(e);
        }

        instMethodList.put("HELLO_WORLD",this::helloWorld);
        instMethodList.put("INVOKE_DONE", this::invokeDone);
        instMethodList.put("INVOKE_OUTPUT", this::invokeOutput);
        instMethodList.put("LOGIN",this::login);
        instMethodList.put("SIGN_UP",this::signUp);
        instMethodList.put("LOGOUT",this::logout);
    }

    /**
     * Tries to execute the given instruction.
     * @param instruction that needs to be executed
     * @param connectionHandler the source of the instruction
     */
    public synchronized void execute(Instruction instruction, ConnectionHandler connectionHandler){
        currentInstruction = instruction;
        currentHandler = connectionHandler;
        currentSocketConnection = connectionHandler.getConnection();

        Runnable method = instMethodList.get(instruction.getName());

        if (method != null){
            method.run();
        } else {
            handleChatProtocolException("Unknown instruction");
        }

        currentInstruction = null;
        currentHandler = null;
        currentSocketConnection = null;
    }

    //region instructionMethods
    private void helloWorld() {
        try{
            currentSocketConnection.writeInstruction(InstructionBuilder.helloWorld());
        } catch (IOException e){
            handleIOException();
        }
    }
    private void invokeDone() {
        try{
            currentSocketConnection.writeInstruction(InstructionBuilder.done());
        } catch (IOException e){
            handleIOException();
        }
    }
    private void invokeOutput(){
        try{
            currentSocketConnection.writeInstruction(InstructionBuilder.output(currentInstruction.getParam("message")));
        } catch (IOException e){
            handleIOException();
        }
    }

    private void login(){
        try{
            if(!checkUser()){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("User is already logged in"));
                return;
            }

            String username = currentInstruction.getParam("username");
            String password = currentInstruction.getParam("password");

            if(username == null || password == null){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("Missing parameter"));
            }

            try{
                User user = sqlConnection.login(username,password);

                if(user != null){
                    currentHandler.setUser(user);
                    currentSocketConnection.writeInstruction(InstructionBuilder.done());
                } else {
                    currentSocketConnection.writeInstruction(InstructionBuilder.error("Wrong username or password"));
                }
            } catch (SQLException e){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("Database error"));
            }

        } catch (IOException e) {
            handleIOException();
        }
    }

    private void signUp(){
        try{
            if(!checkUser()){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("User is already logged in"));
                return;
            }

            String username = currentInstruction.getParam("username");
            String password = currentInstruction.getParam("password");

            if(username == null || password == null){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("Missing parameter"));
            }

            try{
                User user = sqlConnection.signup(username,password);

                if(user != null){
                    currentHandler.setUser(user);
                    currentSocketConnection.writeInstruction(InstructionBuilder.done());
                } else {
                    currentSocketConnection.writeInstruction(InstructionBuilder.error("Something went wrong"));
                }
            } catch (SQLException e){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("Database error"));
            }

        } catch (IOException e) {
            handleIOException();
        }
    }

    private void logout(){
        try{
            if(checkUser()){
                currentSocketConnection.writeInstruction(InstructionBuilder.error("User is not logged in"));
                return;
            }
            currentHandler.setUser(null);
            currentSocketConnection.writeInstruction(InstructionBuilder.done());
        }catch (IOException e){
            handleIOException();
        }
    }
    //endregion

    public void close() {
        try{
            sqlConnection.close();
        } catch (SQLException e){
            // I don't know what to do when the connection even fails to close it's self
            throw new RuntimeException(e);
        }
    }
    private boolean checkUser(){
        return currentHandler.getUser() == null;
    }

    private void handleIOException() {
        System.out.println("IOException thrown");
        try {
            currentSocketConnection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleChatProtocolException(String message){
        try{
            currentSocketConnection.writeInstruction(InstructionBuilder.error(message));
        } catch (IOException e){
            handleIOException();
        }
    }
}
