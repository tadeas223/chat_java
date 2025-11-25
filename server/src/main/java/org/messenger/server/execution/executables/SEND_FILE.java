package org.messenger.server.execution.executables;

import org.messenger.connection.socketData.AuthenticationData;
import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.execution.ServerExecutionBundle;
import org.messenger.server.socketData.AutoDBSaveData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SEND_FILE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;

        if(!serverHandler.containsData(AuthenticationData.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        String username = executionBundle.instruction.getParam("username");
        String contents = executionBundle.instruction.getParam("contents");
        String fileName = executionBundle.instruction.getParam("fileName");

        if(username == null || contents == null || fileName == null){
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
                                        .file(contents, fileName, executionBundle
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

                serverExecutionBundle.sqlConnection.saveFile(contents,fileName, sender,username);

                executionBundle.connection.writeInstruction(InstructionBuilder.done());
            } catch (SQLException e){
                System.out.println(e.getMessage());
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Database Error"));
            }
            return;
        }

        executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not online"));
    }
}
