package org.protocolHandling;

import org.connection.SocketConnection;
import org.protocol.Instruction;
import org.connection.ConnectionHandler;

public class ExecutionBundle {
    public Instruction instruction;
    public ConnectionHandler connectionHandler;
    public SocketConnection connection;

    public ExecutionBundle(Instruction instruction, ConnectionHandler connectionHandler) {
        this.instruction = instruction;
        this.connectionHandler = connectionHandler;
        connection = connectionHandler.getConnection();
    }
}
