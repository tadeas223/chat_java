package org.messenger.protocol.protocolHandling;

import org.messenger.connection.SocketConnection;
import org.messenger.protocol.Instruction;
import org.messenger.connection.ConnectionHandler;

/**
 * This bundle holds everything needed to execute an instruction
 */
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
