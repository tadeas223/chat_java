package org.client.executables;

import org.chat.Message;
import org.client.MessageDB;
import org.protocol.Instruction;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

public class MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {

        MessageDB messageDB = new MessageDB();

        Instruction instruction = executionBundle.instruction;
        String usename = instruction.getParam("sender");
        String message = instruction.getParam("message");

        try{
            messageDB.connect();
            if(!messageDB.containsChat(usename)) {
                messageDB.createChat(usename);
            }

            messageDB.addMessage(new Message(usename,message), usename);

            messageDB.close();
        } catch (SQLException e){
            // :(
            throw new RuntimeException(e);
        }

    }
}
