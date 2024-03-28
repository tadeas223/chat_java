package org.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is used for handling {@link Socket} connection. And for listening for read messages.
 * <h2>Reading</h2>
 * This class contains a thread that should be started by the method startReading().
 * The thread constantly reads from the socket's {@link java.io.InputStream InputStream} and triggers all {@link MsgReadListener MsgReadListeners} with the message.
 * This class can throw a {@link RuntimeException} unexpectedly when the readingThread fails to read the message and then fails to close the {@link Socket}.
 * The read method is not public because it would halt the program or the method would mess the message up. For the method to work properly only one thread should trigger this method at a time and that is the reading thread.
 *
 * <h2>Writing</h2>
 * This class also contains a synchronized method writeString() that can be used to write a message through the socket.
 * This method adds the BREAK_CHAR character at the end of every message to indicate that the message has ended.
 *
 * <p></p>
 * Almost every method throws {@link IOException}, because those method are working with streams and sockets.
 */
public class SocketConnection implements Runnable {
    public static final char BREAK_CHAR = 10;
    public static final int SERVER_PORT = 60000;
    private final ArrayList<MsgReadListener> msgReadListeners = new ArrayList<>();
    private Socket socket;
    private Thread readingThread = new Thread(this, "SocketReadingThread");
    private boolean close = false;

    //region constructors
    public SocketConnection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Creates a new socket for handling
     *
     * @param ip   IP address of the end point
     * @param port PORT of the end point
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public SocketConnection(String ip, int port) throws IOException {
        // Creating a new Socket from the IP and PORT
        socket = new Socket(ip, port);
    }

    /**
     * Creates a new socket with the SERVER_PORT as on port of the socket.
     *
     * @param ip IP address of the end point
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public SocketConnection(String ip) throws IOException {
        socket = new Socket(ip, SERVER_PORT);
    }
    //endregion

    /**
     * Closes the socket and stops the thread.
     * <br></br>
     * This method should be used at the end of the use of this class.
     *
     * @throws IOException when an IO error occurs when trying to close the socket
     */
    public void close() throws IOException {
        // Closing the socket and stopping the readingThread's while loop
        close = true;
        socket.close();
    }

    /**
     * Starts the thread that reads for incoming messages.
     * <br>
     * The thread can only be stopped by the close() method.
     */
    public void startReading() {
        // Nothing very special here
        readingThread.start();
    }

    private void callListeners(String msg) {
        // Calls every listener that a message was read
        for (MsgReadListener listener : msgReadListeners) {

        }
    }

    private synchronized String readString() throws IOException {
        String msg = "";
        int currentChar;

        // Reading the message char by char from the socket's InputStream
        // Until the message contains the BREAK_CHAR than the message is over
        while ((currentChar = socket.getInputStream().read()) != BREAK_CHAR) {
            if (currentChar == -1) {
                // When the char == -1 that means that the connection was closed :(
                throw new IOException("InputStream closed");
            }

            msg += (char) currentChar;
        }

        return msg;
    }

    /**
     * Writes a message to the socket.
     * <br>
     * Adds a BREAK_CHAR character at the end of the message to indicate that the message is over.
     * <br>
     * This method is synchronized to prevent sending more messages at the time - this would end up with a garbled message being sent.
     *
     * @param message that needs to be sent by the socket
     * @throws IOException when an I/O error occurs when trying to write the message to the socket
     */
    public synchronized void writeString(String message) throws IOException {
        // Writing the message char by char
        for (char c : message.toCharArray()) {
            socket.getOutputStream().write(c);
        }

        // Adding the BREAK_CHAR at the end of the message to indicate that the message is over
        socket.getOutputStream().write(BREAK_CHAR);
    }

    //region Get&Set
    public void addMsgReadListener(MsgReadListener listener) {
        msgReadListeners.add(listener);
    }

    public void removeMsgReadListener(MsgReadListener listener) {
        msgReadListeners.remove(listener);
    }

    public ArrayList<MsgReadListener> getMsgReadListeners() {
        return msgReadListeners;
    }

    public Socket getSocket() {
        return socket;
    }
    //endregion

    @Override
    public void run() {
        // This loop will stop only when the close method is run.
        while (!close) {
            try {
                // Constantly reading for incoming messages and notifying listeners that a message came
                String msg = readString();
                callListeners(msg);
            } catch (IOException e) {
                //logging code will be here

                // Close the socket and the thread when an error occurs
                try {
                    close();
                } catch (IOException ex) {
                    // I don't know how to handle this without crashing the program :(
                    // If the socket fails to close then I don't know.
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
