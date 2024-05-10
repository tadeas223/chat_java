package org.server.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.server.ServerExecutionBundle;

import java.io.IOException;

/**
 * This executable returns the id of the currently registered user.
 */
public class GET_ID implements Executable {

    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) serverExecutionBundle.connectionHandler;

        if(!serverHandler.checkUser()){
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        serverExecutionBundle.connection.writeInstruction(
                InstructionBuilder.output(
                        String.valueOf(
                                serverHandler.getUser().getId())));
    }
}
