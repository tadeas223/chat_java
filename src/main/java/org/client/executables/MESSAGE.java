package org.client.executables;

import org.client.Message;
import org.client.MessageDB;
import org.protocol.Instruction;
import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This executable handles the MESSAGE instruction that can be received from the server.
 * It saves the message into a local database - {@link MessageDB}.
 */
public class MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        // Creating a Database object
        MessageDB messageDB = new MessageDB();

        // Getting information from the instruction
        Instruction instruction = executionBundle.instruction;
        String username = instruction.getParam("sender");
        String message = instruction.getParam("message");

        try {
            // Connect to the database
            messageDB.connect();

            // If the message is from a new person - create a new chat for it
            if (!messageDB.containsChat(username)) {
                messageDB.createChat(username);
            }

            // Save the message to the db
            messageDB.addMessage(new Message(username, message), username);

            messageDB.close();
        } catch (SQLException e) {
            // I don't know how to handle this :(
            throw new RuntimeException(e);
        }

    }
}
