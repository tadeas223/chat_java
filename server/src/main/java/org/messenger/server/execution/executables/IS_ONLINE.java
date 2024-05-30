package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.execution.ServerExecutionBundle;
import org.messenger.connection.socketData.AuthenticationData;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This executable indicates if a user is online.
 * The instruction for this executable takes username as a parameter.
 */
public class IS_ONLINE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;

        ServerConnectionHandler serverHandler = (ServerConnectionHandler) serverExecutionBundle.connectionHandler;
        if(!serverHandler.containsData(AuthenticationData.class)){
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        String username = serverExecutionBundle.instruction.getParam("username");

        if(username == null){
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Missing argument"));
            return;
        }

        ArrayList<ServerConnectionHandler> handlers = serverHandler.getServer().getHandlers();

        for(ServerConnectionHandler handler : handlers){
            AuthenticationData authData = handler.getData(AuthenticationData.class);
            if(authData != null){
                if(authData.getUser().getUsername().equals(username)){
                    serverExecutionBundle.connection.writeInstruction(InstructionBuilder.trueInstruction());
                    return;
                }
            }
        }

        serverExecutionBundle.connection.writeInstruction(InstructionBuilder.falseInstruction());
    }
}
