package org.server;

import org.connection.SocketConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    private final ArrayList<ConnectionHandler> handlers = new ArrayList<>();
    private boolean close = false;

    /**
     * Creates a new object of this class and calls the start() method at it.
     * <br>
     * This method can unexpectedly throw {@link RuntimeException} when the start() throws an {@link IOException}.
     *
     * @param args random argument may be used for configuration of the server later
     */
    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the server at the {@link SocketConnection SocketConnection's} SERVER_PORT.
     *
     * @throws IOException if I/O error occurs when listening at the port
     */
    public void start() throws IOException {
        start(SocketConnection.SERVER_PORT);
    }

    /**
     * Starts the server.
     *
     * @param port that the server will be listening at
     * @throws IOException if I/O error occurs when listening at the port
     */
    public void start(int port) throws IOException {
        // Creating a ServerSocket for listening at the port
        ServerSocket serverSocket = new ServerSocket(port);

        // This loop will close only when the close() method is called
        while (!close) {
            // When a socket is caught at the port SocketConnection object is created for it
            SocketConnection connection = new SocketConnection(serverSocket.accept());
            connection.startReading();

            // Creating a handler for the connection and adding it to the handler list
            ConnectionHandler handler = new ConnectionHandler(connection);
            addConnectionHandler(handler);
        }
    }

    /**
     * Stops the server.
     */
    public void close() {
        close = true;
    }

    //region Get&Set

    public ArrayList<ConnectionHandler> getHandlers() {
        return handlers;
    }

    public void addConnectionHandler(ConnectionHandler handler) {
        handlers.add(handler);
    }

    public void removeConnectionHandler(ConnectionHandler handler) {
        handlers.remove(handler);
    }
    //endregion
}
