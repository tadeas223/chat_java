package org.server.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;

import java.io.IOException;

public class LOGOUT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;

        if(serverHandler.checkUser()){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is already logged in"));
            return;
        }

        serverHandler.setUser(null);

        executionBundle.connection.writeInstruction(InstructionBuilder.done());
    }
}
