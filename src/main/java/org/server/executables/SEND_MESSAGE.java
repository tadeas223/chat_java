package org.server.executables;

import org.protocol.InstructionBuilder;
import org.server.ServerConnectionHandler;
import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import java.io.IOException;
import java.util.ArrayList;

public class SEND_MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;

        if(!serverHandler.checkUser()){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        String username = executionBundle.instruction.getParam("username");
        String message = executionBundle.instruction.getParam("message");

        if(username == null || message == null){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Missing argument"));
            return;
        }

        ArrayList<ServerConnectionHandler> handlers = serverHandler.getServer().getHandlers();

        for(ServerConnectionHandler handler : handlers){
            if(handler.getUser() != null){
                if(handler.getUser().getUsername().equals(username)){
                    handler.getConnection()
                            .writeInstruction(InstructionBuilder
                                    .message(message, serverHandler
                                            .getUser()
                                            .getUsername()));

                    executionBundle.connection.writeInstruction(InstructionBuilder.done());
                    return;
                }
            }
        }

        executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not online"));
    }
}
