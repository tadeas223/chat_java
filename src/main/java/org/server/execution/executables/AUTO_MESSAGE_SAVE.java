package org.server.execution.executables;

import org.connection.SocketConnection;
import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.server.execution.ServerExecutionBundle;
import org.server.socketData.AutoDBSaveData;

import java.io.IOException;

public class AUTO_MESSAGE_SAVE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;
        ServerConnectionHandler handler = (ServerConnectionHandler) serverExecutionBundle.connectionHandler;
        SocketConnection connection = serverExecutionBundle.connection;

        String value = executionBundle.instruction.getParam("value");

        if(value == null){
            connection.writeInstruction(InstructionBuilder.error("Missing Argument"));
            return;
        }

        if(value.equals("true")){
            handler.getData(AutoDBSaveData.class).setAutoSave(true);
        }
        else {
            handler.getData(AutoDBSaveData.class).setAutoSave(false);
        }
    }
}
