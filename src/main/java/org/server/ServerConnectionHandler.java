package org.server;

import org.connection.ConnectionHandler;
import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.InvalidStringException;
import org.protocol.ProtocolTranslator;
import org.protocol.protocolHandling.MissingDefaultException;
import org.security.User;
import org.server.execution.ServerExecutor;
import org.server.socketData.SocketData;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used for handling incoming messages from {@link SocketConnection}.
 * <br>
 * When this class is instantiated, it adds itself as an {@link MsgReadListener} into socketConnection.
 * <br>
 * When this class is no longer used, the close() or closeSocket() method should be called.
 */
public class ServerConnectionHandler extends ConnectionHandler {
    private final Server server;
    private ArrayList<SocketData> socketDataList;
    public ServerConnectionHandler(SocketConnection connection, Server server, ServerExecutor serverExecutor) throws IOException {
        if(serverExecutor == null){
            this.executor = new ServerExecutor();
        } else {
            this.executor = serverExecutor;
        }

        this.server = server;
        this.connection = connection;
        connection.addMsgReadListener(this);

        try {
            executor.execute(new Instruction("INIT"),this);
        } catch (MissingDefaultException e) {
            connection.writeInstruction(InstructionBuilder.error("Impossible error"));
            throw new RuntimeException(e);
        }
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
            server.getLog().println("[" + connection.getIp() + "] REQUEST \"" + msg + "\"");
            Instruction instruction = ProtocolTranslator.decode(msg);

            executor.execute(instruction, this);
            server.getLog().println("[" + connection.getIp() + "] - REQUEST HANDLED");

        } catch (InvalidStringException e){
            try{
                server.getLog().println("[" + connection.getIp() + "] - Invalid Instruction");
                connection.writeInstruction(InstructionBuilder.error("Invalid instruction"));
            } catch (IOException ex){
                throw new RuntimeException(ex);
            }
        } catch (IOException e){
            try{
                server.getLog().println("[" + connection.getIp() + "] - Closing Socket");
                closeSocket();
            } catch (IOException ex){
                // Stop the program if the socket fails to close
                throw new RuntimeException(ex);
            }
        } catch (MissingDefaultException e) {
            // Default executable instruction is missing
            missingDefaultExceptionHandle();
        }
    }

    private void missingDefaultExceptionHandle(){
        try {
            connection.writeInstruction(InstructionBuilder.error("Missing Default Instruction"));
        } catch (IOException ex) {
            // If this fails - disconnect
            server.getLog().println("[" + connection.getIp() + "] - Failed to write instruction = Closing Session");
            try {
                closeSocket();
            } catch (IOException exc) {
                // :(
                throw new RuntimeException(exc);
            }
        }
    }

    public <T extends SocketData> T getData(Class<T> dataClass){
        for(SocketData sd : socketDataList){
            if(sd.getClass().equals(dataClass)){
                return (T) sd;
            }
        }
        return null;
    }

    public boolean addData(SocketData socketData){
        for(SocketData s : socketDataList){
            if(s.getClass().equals(socketData.getClass())){
              return false;
            }
        }

        socketDataList.add(socketData);
        return true;
    }

    public <T extends SocketData>boolean containsData(Class<T> dataClass){
        for(SocketData s : socketDataList){
            if(s.getClass().equals(dataClass)){
                return true;
            }
        }
        return false;
    }

    public <T extends SocketData> void removeData(Class<T> dataClass){
        for(SocketData s : socketDataList){
            if(s.getClass().equals(dataClass)){
               socketDataList.remove(s);
            }
        }
    }

    public Server getServer() {
        return server;
    }
}
