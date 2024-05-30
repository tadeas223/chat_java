package org.messenger.connection;

import org.messenger.connection.socketData.SocketData;
import org.messenger.protocol.protocolHandling.InstructionExecutor;
import org.messenger.protocol.protocolHandling.MissingDefaultException;
import org.messenger.protocol.Instruction;
import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.InvalidStringException;
import org.messenger.protocol.ProtocolTranslator;

import java.io.IOException;
import java.util.HashSet;

/**
 * This class is used for handling connection between a client and a server.
 */
public class ConnectionHandler implements MsgReadListener {
    protected SocketConnection connection;
    protected InstructionExecutor executor;
    protected final HashSet<SocketData> socketDataList = new HashSet<>();

    @Override
    public void messageRead(String msg) {
        handle(msg);
    }

    /**
     * This method executes the appropriate executable for the request.
     * @param msg the request
     */
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
            missingDefaultExceptionHandle();
        }
    }

    private void missingDefaultExceptionHandle(){
        try {
            connection.writeInstruction(InstructionBuilder.error("Missing Default Instruction"));
        } catch (IOException ex) {
            // If this fails - disconnect
            try {
                closeSocket();
            } catch (IOException exc) {
                // :(
                throw new RuntimeException(exc);
            }
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

    public <T extends SocketData> T getData(Class<T> dataClass){
        synchronized (socketDataList){
            for(SocketData sd : socketDataList){
                if(sd.getClass().equals(dataClass)){
                    return (T) sd;
                }
            }
            return null;
        }
    }

    public boolean addData(SocketData socketData){
        synchronized (socketDataList){
            for(SocketData s : socketDataList){
                if(s.getClass().equals(socketData.getClass())){
                    return false;
                }
            }

            socketDataList.add(socketData);
            return true;
        }
    }

    public  <T extends SocketData>boolean containsData(Class<T> dataClass){
        synchronized (socketDataList){
            for(SocketData s : socketDataList){
                if(s.getClass().equals(dataClass)){
                    return true;
                }
            }
            return false;
        }
    }

    public  <T extends SocketData> void removeData(Class<T> dataClass){
        synchronized (socketDataList){
            socketDataList.removeIf(s -> s.getClass().equals(dataClass));
        }
    }
}
