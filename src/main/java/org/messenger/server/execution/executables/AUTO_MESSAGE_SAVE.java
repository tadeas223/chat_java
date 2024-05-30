package org.messenger.server.execution.executables;

import org.messenger.connection.SocketConnection;
import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.execution.ServerExecutionBundle;
import org.messenger.server.socketData.AutoDBSaveData;

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

        connection.writeInstruction(InstructionBuilder.done());
    }
}
