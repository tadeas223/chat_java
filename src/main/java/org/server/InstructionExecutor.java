package org.server;

import org.connection.SocketConnection;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class is used for executing code for a specific instructions.
 */
public class InstructionExecutor {
    private HashMap<String,Runnable> instMethodList = new HashMap<>();
    private Instruction currentInstruction = null;
    private SocketConnection currentSocketConnection = null;

    public InstructionExecutor(){
        instMethodList.put("HELLO_WORLD",this::helloWorld);
        instMethodList.put("INVOKE_DONE", this::invokeDone);
        instMethodList.put("INVOKE_OUTPUT", this::invokeOutput);
    }

    /**
     * Tries to execute the given instruction.
     * @param instruction that needs to be executed
     * @param socketConnection the source of the instruction
     */
    public synchronized void execute(Instruction instruction, SocketConnection socketConnection){
        currentInstruction = instruction;
        currentSocketConnection = socketConnection;

        Runnable method = instMethodList.get(instruction.getName());

        if (method != null){
            method.run();
        } else {
            handleChatProtocolException("Unknown instruction");
        }

        currentInstruction = null;
        currentSocketConnection = null;
    }

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
