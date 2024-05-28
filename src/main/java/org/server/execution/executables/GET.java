package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.server.socketData.ArrayReading;

import java.io.IOException;

public class GET implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler handler = (ServerConnectionHandler) executionBundle.connectionHandler;

        int index = Integer.parseInt(executionBundle.instruction.getParam("index"));

        if(!handler.containsData(ArrayReading.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Array Reading not initialized"));
            return;
        }

        ArrayReading arrayReading = handler.getData(ArrayReading.class);

        try{
            executionBundle.connection.writeInstruction(arrayReading.get(index));
        } catch (IndexOutOfBoundsException e){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Index out of bounds"));
        }
    }
}