package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.server.socketData.AutoDBSaveData;
import org.server.socketData.InitCallData;

import java.io.IOException;

public class INIT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler handler = (ServerConnectionHandler) executionBundle.connectionHandler;

        if(handler.containsData(InitCallData.class)){
            if(handler.getData(InitCallData.class).isInitCalled()){
                executionBundle.connection.writeInstruction(InstructionBuilder.error("Illegal instruction call"));
                return;
            }
        }

        handler.addData(new AutoDBSaveData(false));


        handler.addData(new InitCallData(true));
    }
}
