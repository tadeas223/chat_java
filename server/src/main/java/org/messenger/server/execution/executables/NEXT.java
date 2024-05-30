package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.socketData.ArrayReading;

import java.io.IOException;

public class NEXT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler handler = (ServerConnectionHandler) executionBundle.connectionHandler;

        if(!handler.containsData(ArrayReading.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Array Reading not initialized"));
            return;
        }

        ArrayReading arrayReading = handler.getData(ArrayReading.class);

        if(arrayReading.hasNext()){
            executionBundle.connection.writeInstruction(arrayReading.next());
        } else {
            executionBundle.connection.writeInstruction(InstructionBuilder.end());
        }
    }
}
