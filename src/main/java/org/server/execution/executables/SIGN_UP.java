package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.security.User;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.server.execution.ServerExecutionBundle;
import org.connection.socketData.AuthenticationData;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This executable registers the user and then logs him in.
 * The instruction for this executable takes in parameters password and username.
 */
public class SIGN_UP implements Executable {
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

        if (username == null || password == null) {
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Missing parameter"));
            return;
        }

        try {
            User user = serverExecutionBundle.sqlConnection.signup(username, password);

            if (user != null) {
                AuthenticationData authData = new AuthenticationData(user);
                serverHandler.addData(authData);
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.done());
            } else {
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Something went wrong"));
            }
        } catch (SQLException e) {
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
        }
    }
}
