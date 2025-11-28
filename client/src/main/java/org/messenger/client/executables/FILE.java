package org.messenger.client.executables;

import org.messenger.chat.File;
import org.messenger.chat.Message;
import org.messenger.client.ClientConnectionHandler;
import org.messenger.client.ClientNotLoggedInException;
import org.messenger.client.MessageDB;
import org.messenger.client.socketData.MessageListenerData;
import org.messenger.protocol.Instruction;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;
import java.sql.SQLException;

public class FILE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        ClientConnectionHandler clientHandler = (ClientConnectionHandler) executionBundle.connectionHandler;

        // temporary
        System.out.println("FILE RECEIVED");

        Instruction instruction = executionBundle.instruction;
        String username = instruction.getParam("sender");
        String contents = instruction.getParam("contents");
        String fileName = instruction.getParam("fileName");

        try {
            MessageDB messageDB = clientHandler.getClient().getDatabase();

            if (!messageDB.containsChat(username)) {
                messageDB.createChat(username);
            }

            messageDB.addFile(clientHandler.getClient().getUser(), new File(username, fileName), contents, username);

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
