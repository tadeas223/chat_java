package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.security.User;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.execution.ServerExecutionBundle;
import org.messenger.connection.socketData.AuthenticationData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class logs in a user.
 * The instruction for this executable takes in parameter username and password.
 */
public class LOGIN implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) serverExecutionBundle.connectionHandler;

        if (serverHandler.containsData(AuthenticationData.class)) {
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("User is already logged in"));
            return;
        }



        String username = serverExecutionBundle.instruction.getParam("username");
        String password = serverExecutionBundle.instruction.getParam("password");

        ArrayList<ServerConnectionHandler> handlers = serverHandler.getServer().getHandlers();
        for(ServerConnectionHandler handler : handlers){
            AuthenticationData authData = handler.getData(AuthenticationData.class);
            if(authData != null){
                if(authData.getUser().getUsername().equals(username)){
                    serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("User is already logged in"));
                    return;
                }
            }
        }

        if (username == null || password == null) {
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Missing parameter"));
            return;
        }

        try {
            User user = serverExecutionBundle.sqlConnection.login(username,password);

            if (user != null) {
                AuthenticationData authData = new AuthenticationData(user);
                serverHandler.addData(authData);
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.done());
            } else {
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Wrong username or password"));
            }
        } catch (SQLException e) {
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
        }
    }
}
