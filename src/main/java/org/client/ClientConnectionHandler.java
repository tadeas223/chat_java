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

/**
 * This class handles incoming message from a server.
 * This class is used in {@link Client} for message handling.
 */
public class ClientConnectionHandler extends ConnectionHandler {
    private final SocketConnection connection;
    private final Client client;
    private final ClientExecutor executor = new ClientExecutor();

    /**
     * Sets the client to handle messages for.
     * @param client to handle messages for
     */
    public ClientConnectionHandler(Client client) {
        this.client = client;
        this.connection = client.getSocketConnection();
    }

    @Override
    public void messageRead(String msg) {
        try{
            // Convert the message to instruction
            Instruction instruction = ProtocolTranslator.decode(msg);

            // run the correct executable for the instruction
            executor.execute(instruction, this);
        } catch (InvalidStringException | IOException | MissingDefaultException e){
            try{
                // Sent an error message when anything fails
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
