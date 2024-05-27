package org.server.execution.executables;

import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.security.User;
import org.server.SQLConnection;
import org.server.ServerConnectionHandler;
import org.server.execution.ServerExecutionBundle;
import org.connection.socketData.AuthenticationData;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This executable saves a message to a database.
 * The instruction for this executable takes parameter username and message.
 */
public class SAVE_TO_DATABASE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;

        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;
        SQLConnection sqlConnection = serverExecutionBundle.sqlConnection;

        Instruction instruction = executionBundle.instruction;

        if(!serverHandler.containsData(AuthenticationData.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }

        User user = serverHandler.getData(AuthenticationData.class).getUser();

        String sender = user.getUsername();
        String receiver = instruction.getParam("username");
        String message = instruction.getParam("message");


        if(receiver == null || message == null){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Missing parameter"));
            return;
        }

        try{
            if(!serverExecutionBundle.sqlConnection.userExists(receiver)){
                executionBundle.connection.writeInstruction(InstructionBuilder.error("User does not exist"));
                return;
            }

            sqlConnection.saveMessage(message,sender,receiver);
        } catch (SQLException e){
            e.printStackTrace();
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
            return;
        }

        executionBundle.connection.writeInstruction(InstructionBuilder.done());
    }
}
