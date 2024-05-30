package org.messenger.client;

import org.messenger.connection.ConnectionHandler;
import org.messenger.connection.SocketConnection;
import org.messenger.protocol.Instruction;
import org.messenger.protocol.InvalidStringException;
import org.messenger.protocol.ProtocolTranslator;
import org.messenger.protocol.protocolHandling.MissingDefaultException;

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

        try {
            executor.execute(new Instruction("INIT"),this);
        } catch (IOException | MissingDefaultException e) {
            // AHA SMŮLA :(
            throw new RuntimeException(e);
        }
    }

    @Override
    public void messageRead(String msg) {
        try{
            Instruction instruction = ProtocolTranslator.decode(msg);

            executor.execute(instruction, this);
        } catch (InvalidStringException | IOException | MissingDefaultException e){
//            try{
//                connection.writeInstruction(InstructionBuilder.error("Invalid instruction"));
//            } catch (IOException ex){
//                // Failed to send an error message
//                throw new RuntimeException(ex);
//            }
        }
    }

    public Client getClient() {
        return client;
    }
}
