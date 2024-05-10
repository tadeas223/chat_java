package org.protocol.protocolHandling;

import org.protocol.Instruction;
import org.connection.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class is used for executing code for specific instructions.
 */
public class InstructionExecutor {
    protected final HashMap<String, Executable> instMethodList = new HashMap<>();

    /**
     * Tries to execute the given instruction.
     * @param instruction instruction that needs to be executed, it the instruction's {@link Executable} is missing, the default {@link Executable} is run
     * @param connectionHandler the source of the instruction
     * @throws IOException if an I/O error occurs when writing to the connectionHandler's socket
     */
    public synchronized void execute(Instruction instruction, ConnectionHandler connectionHandler) throws IOException, MissingDefaultException {
        Executable executable = instMethodList.get(instruction.getName());

        ExecutionBundle executionBundle = new ExecutionBundle(instruction, connectionHandler);

        executeExecutable(executable,executionBundle);
    }

    public synchronized void executeExecutable(Executable executable,ExecutionBundle executionBundle) throws IOException, MissingDefaultException {
        if (executable != null) {
            try {
                executable.execute(executionBundle);
            } catch (IOException e) {
                throw new IOException(e);
            }
        } else {
            try{
                instMethodList.get("DEFAULT").execute(executionBundle);
            } catch (NullPointerException e){
                throw new MissingDefaultException("Missing default executable");
            }
        }
    }

    public void addExecutable(String instructionName,Executable executable){
        instMethodList.put(instructionName,executable);
    }

    public void removeExecutable(String instructionName){
        instMethodList.remove(instructionName);
    }
}
