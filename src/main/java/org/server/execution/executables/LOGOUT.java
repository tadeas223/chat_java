package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.connection.socketData.AuthenticationData;

import java.io.IOException;

/**
 * This executable logs out the user.
 * The instruction for this executable does not take any parameters.
 */
public class LOGOUT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;

        if(!serverHandler.containsData(AuthenticationData.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        serverHandler.removeData(AuthenticationData.class);

        executionBundle.connection.writeInstruction(InstructionBuilder.done());
    }
}
