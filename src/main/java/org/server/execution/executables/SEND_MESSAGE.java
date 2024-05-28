package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.server.ServerConnectionHandler;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.execution.ServerExecutionBundle;
import org.connection.socketData.AuthenticationData;
import org.server.socketData.AutoDBSaveData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This executable sends a message to different user.
 * The instruction for this executable takes in parameters username and message.
 */
public class SEND_MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;

        if(!serverHandler.containsData(AuthenticationData.class)){
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

        boolean isOnline = false;
        for(ServerConnectionHandler handler : handlers){
            AuthenticationData authData = handler.getData(AuthenticationData.class);
            if(authData != null){
                if(authData.getUser().getUsername().equals(username)){
                    isOnline = true;
                    break;
                }
            }
        }

        AutoDBSaveData autoSave = serverHandler.getData(AutoDBSaveData.class);

        if(isOnline){
            for(ServerConnectionHandler handler : handlers){
                AuthenticationData authData = handler.getData(AuthenticationData.class);
                if(authData != null){
                    if(authData.getUser().getUsername().equals(username)){
                        handler.getConnection()
                                .writeInstruction(InstructionBuilder
                                        .message(message, executionBundle
                                                .connectionHandler
                                                .getData(AuthenticationData.class).getUser().getUsername()));

                        executionBundle.connection.writeInstruction(InstructionBuilder.done());
                        return;
                    }
                }
            }
            return;
        } else if(autoSave.isAutoSave()){
            String sender = serverHandler
                    .getData(AuthenticationData.class)
                    .getUser()
                    .getUsername();

            try{
                if(!serverExecutionBundle.sqlConnection.userExists(username)){
                    executionBundle.connection.writeInstruction(InstructionBuilder.error("User does not exist"));
                    return;
                }

                serverExecutionBundle.sqlConnection.saveMessage(message,sender,username);
            } catch (SQLException e){
                System.out.println(e.getMessage());
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Database Error"));
            }
            return;
        }

        executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not online"));
    }
}
