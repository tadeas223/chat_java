package org.server.executables;

import org.protocol.InstructionBuilder;
import org.security.User;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.ServerConnectionHandler;
import org.server.ServerExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

public class LOGIN implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) serverExecutionBundle.connectionHandler;

        if (serverHandler.checkUser()) {
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
            User user = serverExecutionBundle.sqlConnection.login(username, password);

            if (user != null) {
                serverHandler.setUser(user);
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.done());
            } else {
                serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Wrong username or password"));
            }
        } catch (SQLException e) {
            serverExecutionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
        }
    }
}
