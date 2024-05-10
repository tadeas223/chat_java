package org.server;

import org.connection.SocketConnection;
import org.server.log.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * This class is used for a server.
 * It also contains a main() method for easy execution.
 */
public class Server {
    private final ArrayList<ServerConnectionHandler> handlers = new ArrayList<>();
    private final ServerExecutor serverExecutor = new ServerExecutor();
    private final Log log = new Log();
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
        } finally {
            server.close();
        }
    }

    /**
     * Starts the server at the {@link SocketConnection SocketConnection's} SERVER_PORT.
     *
     * @throws IOException if I/O error occurs when listening at the port
     * @see SocketConnection
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
        log.setConsoleStream(System.out);

        log.println("---------Messenger SERVER---------");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            log.println("ServerSocket at port " + port + " configured");
            catchConnections(serverSocket);
        } catch (IOException e) {
            // There is nothing else a can do when the port listening is not working
            log.println("Failed to open port " + port);
            throw new RuntimeException(e);
        } finally {
            // This is just to remove a warning
            assert serverSocket != null;

            log.println("Closing ServerSocket at port " + port);
            serverSocket.close();
        }

    }

    /**
     * Method used for catching every connection that is trying to access the server.
     * This method contains a while loop that will be iterating until the close() method is called.
     *
     * @param serverSocket that should be used for catching (accepting) the connections
     */
    public void catchConnections(ServerSocket serverSocket) throws IOException {
        log.println("Starting socket listening");

        // This loop will close only when the close() method is called
        while (!close) {
            SocketConnection connection = new SocketConnection(serverSocket.accept());
            log.println("Accepted client at - " + connection.getSocket().getInetAddress().toString());

            connection.startReading();

            // Creating a handler for the connection and adding it to the handler list
            ServerConnectionHandler handler = new ServerConnectionHandler(connection, this,serverExecutor);
            addConnectionHandler(handler);
        }
    }

    /**
     * Stops the server.
     */
    public void close() {
        log.println("Server stop");
        close = true;
    }

    //region Get&Set
    public ArrayList<ServerConnectionHandler> getHandlers() {
        return handlers;
    }

    public Log getLog() {
        return log;
    }

    public void addConnectionHandler(ServerConnectionHandler handler) {
        handlers.add(handler);
    }

    public void removeConnectionHandler(ServerConnectionHandler handler) {
        handlers.remove(handler);
    }
    //endregion
}
