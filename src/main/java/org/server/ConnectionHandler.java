package org.server;

import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.InvalidStringException;
import org.protocol.ProtocolTranslator;

import java.io.IOException;

/**
 * This class is used for handling incoming messages from {@link SocketConnection}.
 * <br>
 * When this class is instantiated, it adds itself as an {@link MsgReadListener} into socketConnection.
 * <br>
 * When this class is no longer used, the close() or closeSocket() method should be called.
 */
public class ConnectionHandler implements MsgReadListener {
    private final Server server;
    private final SocketConnection connection;
    private InstructionExecutor instructionExecutor = new InstructionExecutor();
    public ConnectionHandler(SocketConnection connection, Server server) {
        this.server = server;
        this.connection = connection;
        connection.addMsgReadListener(this);
    }

    /**
     * Removes its self from the listener list in the socketConnection and
     * from the server's handler list if it is present there.
     */
    public void close() {
        server.removeConnectionHandler(this);
        connection.removeMsgReadListener(this);
    }

    /**
     * Runs the close() method. And attempts to close the socketConnection.
     *
     * @throws IOException if an I/O error occurs when trying to close the socketConnection.
     */
    public void closeSocket() throws IOException {
        close();
        connection.close();
    }

    /**
     * Runs the handle method and puts the msg parameter into it.
     *
     * @param msg the message that was read
     */
    @Override
    public void messageRead(String msg) {
        handle(msg);
    }

    /**
     * Handles the incoming message and runs an appropriate code that should be run to for a tne message to be handled.
     *
     * @param msg incoming message
     */
    public void handle(String msg) {
        try{
            Instruction instruction = ProtocolTranslator.decode(msg);

            instructionExecutor.execute(instruction, connection);

        } catch (InvalidStringException e){
            try{
                connection.writeInstruction(InstructionBuilder.error("Invalid instruction"));
            } catch (IOException ex){
                throw new RuntimeException(ex);
            }
        }
    }
}
