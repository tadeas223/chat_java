package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.socketData.AutoDBSaveData;
import org.messenger.server.socketData.InitCallData;

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
