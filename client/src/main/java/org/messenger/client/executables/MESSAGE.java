package org.messenger.client.executables;

import org.messenger.chat.Message;
import org.messenger.client.ClientConnectionHandler;
import org.messenger.client.ClientNotLoggedInException;
import org.messenger.client.MessageDB;
import org.messenger.client.socketData.MessageListenerData;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.protocol.Instruction;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This executable handles the MESSAGE instruction that can be received from the server.
 * It saves the message into a local database - {@link MessageDB}.
 */
public class MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ClientConnectionHandler clientHandler = (ClientConnectionHandler) executionBundle.connectionHandler;


        Instruction instruction = executionBundle.instruction;
        String username = instruction.getParam("sender");
        String message = instruction.getParam("message");

        try {
            MessageDB messageDB = clientHandler.getClient().getDatabase();

            if (!messageDB.containsChat(username)) {
                messageDB.createChat(username);
            }

            messageDB.addMessage(new Message(username, message), username);

            messageDB.close();
        } catch (SQLException e) {
            // I don't know how to handle this :(
            throw new RuntimeException(e);
        } catch (ClientNotLoggedInException e) {
            // This should not happen
        }

        clientHandler.getData(MessageListenerData.class).callListeners();
    }
}
