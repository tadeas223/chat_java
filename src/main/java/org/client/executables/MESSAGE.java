package org.client.executables;

import org.client.Client;
import org.client.ClientConnectionHandler;
import org.client.Message;
import org.client.MessageDB;
import org.protocol.Instruction;
import org.protocol.ParamList;
import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import javax.print.MultiDocPrintService;
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
