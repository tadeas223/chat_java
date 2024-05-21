package org.connection;

import org.connection.socketData.SocketData;
import org.protocol.Instruction;
import org.protocol.InstructionBuilder;
import org.protocol.InvalidStringException;
import org.protocol.ProtocolTranslator;
import org.protocol.protocolHandling.InstructionExecutor;
import org.protocol.protocolHandling.MissingDefaultException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used for handling connection between a client and a server.
 */
public class ConnectionHandler implements MsgReadListener {
    protected SocketConnection connection;
    protected InstructionExecutor executor;
    protected ArrayList<SocketData> socketDataList = new ArrayList<>();

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
}
