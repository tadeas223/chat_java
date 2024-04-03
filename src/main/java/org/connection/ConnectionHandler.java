package org.connection;

import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.InvalidStringException;
import org.protocol.ProtocolTranslator;
import org.protocolHandling.InstructionExecutor;
import org.protocolHandling.MissingDefaultException;

import java.io.IOException;

public class ConnectionHandler implements MsgReadListener {
    protected SocketConnection connection;
    protected InstructionExecutor executor;

    @Override
    public void messageRead(String msg) {
        handle(msg);
    }

    public void handle(String msg){
        try{
            Instruction instruction = ProtocolTranslator.decode(msg);

            executor.execute(instruction, this);

        } catch (InvalidStringException e){
            try{
                connection.writeInstruction(InstructionBuilder.error("Invalid instruction"));
            } catch (IOException ex){
                throw new RuntimeException(ex);
            }
        } catch (IOException e){
            try{
                closeSocket();
            } catch (IOException ex){
                // Stop the program if the socket fails to close
                throw new RuntimeException(ex);
            }
        } catch (MissingDefaultException e) {
            // Default executable instruction is missing
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes its self from the listener list in the socketConnection and
     * from the server's handler list if it is present there.
     */
    public void close() {
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

    public SocketConnection getConnection() {
        return connection;
    }

    public InstructionExecutor getExecutor() {
        return executor;
    }
}
