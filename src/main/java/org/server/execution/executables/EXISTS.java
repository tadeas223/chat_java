package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.execution.ServerExecutionBundle;

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
