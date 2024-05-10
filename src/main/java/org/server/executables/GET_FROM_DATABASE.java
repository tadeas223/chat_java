package org.server.executables;

import org.chat.Message;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;
import org.server.SQLConnection;
import org.server.ServerConnectionHandler;
import org.server.ServerExecutionBundle;

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

        Message[] messages = new Message[0];

        if(!serverHandler.checkUser()){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("User is not logged in"));
            return;
        }
        try{
            messages = sqlConnection.getMessages(serverHandler.getUser().getUsername());
        } catch (SQLException e){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Database error"));
            return;
        }

        ArrayList<Instruction> instructionList = new ArrayList<>();

        for(Message m : messages){
            instructionList.add(InstructionBuilder.message(m.getMessage(),m.getUsername()));
        }

        Instruction[] instructions = instructionList.toArray(Instruction[]::new);

        instructions = InstructionBuilder.messageArrayWrap(instructions);

        for(Instruction i : instructions){
            executionBundle.connection.writeInstruction(i);
        }
    }
}
