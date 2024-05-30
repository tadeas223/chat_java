package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.execution.ServerExecutionBundle;
import org.messenger.connection.socketData.AuthenticationData;

import java.io.IOException;

/**
 * This executable returns the id of the currently registered user.
 */
public class GET_ID implements Executable {

    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) serverExecutionBundle.connectionHandler;

        if(!serverHandler.containsData(AuthenticationData.class)){
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        serverExecutionBundle.connection.writeInstruction(
                InstructionBuilder.output(
                        String.valueOf(
                                serverHandler.getData(AuthenticationData.class).getUser().getId())));
    }
}
