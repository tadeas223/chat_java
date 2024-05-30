package org.messenger.server.execution.executables;

import org.messenger.chat.Message;
import org.messenger.protocol.Instruction;
import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.server.SQLConnection;
import org.messenger.server.ServerConnectionHandler;
import org.messenger.server.execution.ServerExecutionBundle;
import org.messenger.connection.socketData.AuthenticationData;
import org.messenger.server.socketData.ArrayReading;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This executable returns all messages for the logged-in user.
 */
public class GET_FROM_DATABASE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ServerExecutionBundle serverExecutionBundle = (ServerExecutionBundle) executionBundle;
        ServerConnectionHandler serverHandler = (ServerConnectionHandler) executionBundle.connectionHandler;
        SQLConnection sqlConnection = serverExecutionBundle.sqlConnection;

        Message[] messages;

        if(!serverHandler.containsData(AuthenticationData.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }
        try{
            messages = sqlConnection.getMessages(serverHandler
                    .getData(AuthenticationData.class)
                    .getUser()
                    .getUsername());

        } catch (SQLException e){
            System.out.println(e.getMessage());

            executionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
            return;
        }

        ArrayList<Instruction> instructionList = new ArrayList<>();

        for(Message m : messages){
            instructionList.add(InstructionBuilder.message(m.getMessage(),m.getUsername()));
        }

        Instruction[] instructions = instructionList.toArray(Instruction[]::new);

        ArrayReading arrayReading = new ArrayReading(instructions);

        if(serverHandler.containsData(ArrayReading.class)){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Array Reading already in process"));
        } else{
            executionBundle.connection.writeInstruction(InstructionBuilder.array(instructions.length));
            serverHandler.addData(arrayReading);
        }
    }
}
