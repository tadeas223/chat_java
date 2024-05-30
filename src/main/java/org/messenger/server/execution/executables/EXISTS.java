package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.execution.ServerExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

public class EXISTS implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverBundle = (ServerExecutionBundle) executionBundle;

        String username = executionBundle.instruction.getParam("username");

        if(username == null){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Missing parameter"));
            return;
        }

        try{
            if(serverBundle.sqlConnection.userExists(username)) {
                executionBundle.connection.writeInstruction(InstructionBuilder.trueInstruction());
            } else {
                executionBundle.connection.writeInstruction(InstructionBuilder.falseInstruction());
            }
        } catch (SQLException e){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
        }

    }
}
