package org.server.executables;

import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.security.User;
import org.server.SQLConnection;
import org.server.ServerConnectionHandler;
import org.server.ServerExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

public class SAVE_TO_DATABASE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;

        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;
        SQLConnection sqlConnection = serverExecutionBundle.sqlConnection;

        User user = serverHandler.getUser();

        Instruction instruction = executionBundle.instruction;

        String sender = user.getUsername();
        String receiver = instruction.getParam("username");
        String message = instruction.getParam("message");

        try{
            sqlConnection.saveMessage(sender,receiver,message);
        } catch (SQLException e){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
            return;
        }

        executionBundle.connection.writeInstruction(InstructionBuilder.invokeDone());
    }
}
