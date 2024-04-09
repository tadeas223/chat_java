package org.client;

import org.connection.ConnectionHandler;
import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.InvalidStringException;
import org.protocol.ProtocolTranslator;
import org.protocolHandling.MissingDefaultException;

import java.io.IOException;

public class ClientConnectionHandler extends ConnectionHandler {
    private final SocketConnection connection;
    private final Client client;

    private final ClientExecutor executor = new ClientExecutor();
    public ClientConnectionHandler(Client client) {
        this.client = client;
        this.connection = client.getSocketConnection();
    }

    @Override
    public void messageRead(String msg) {
        try{
            Instruction instruction = ProtocolTranslator.decode(msg);

            executor.execute(instruction, this);
        } catch (InvalidStringException | IOException | MissingDefaultException e){
            try{
                connection.writeInstruction(InstructionBuilder.error("Invalid instruction"));
            } catch (IOException ex){
                // Failed to send an error message
                throw new RuntimeException(ex);
            }
        }
    }

    public Client getClient() {
        return client;
    }
}
