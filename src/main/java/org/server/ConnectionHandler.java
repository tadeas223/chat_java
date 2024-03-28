package org.server;

import org.connection.SocketConnection;

public class ConnectionHandler {
    private SocketConnection connection;

    public ConnectionHandler(SocketConnection connection) {
        this.connection = connection;
    }
}
