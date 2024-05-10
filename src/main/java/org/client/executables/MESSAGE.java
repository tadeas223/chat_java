package org.client.executables;

import org.chat.Message;
import org.client.MessageDB;
import org.protocol.Instruction;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This executable handles the MESSAGE instruction that can be received from the server.
 * It saves the message into a local database - {@link MessageDB}.
 */
public class MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        MessageDB messageDB = new MessageDB();

        Instruction instruction = executionBundle.instruction;
        String username = instruction.getParam("sender");
        String message = instruction.getParam("message");

        try {
            messageDB.connect();

            if (!messageDB.containsChat(username)) {
                messageDB.createChat(username);
            }

            messageDB.addMessage(new Message(username, message), username);

            messageDB.close();
        } catch (SQLException e) {
            // I don't know how to handle this :(
            throw new RuntimeException(e);
        }

    }
}
