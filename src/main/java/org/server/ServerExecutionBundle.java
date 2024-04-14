package org.server;

import org.protocol.Instruction;
import org.protocol.protocolHandling.ExecutionBundle;

public class ServerExecutionBundle extends ExecutionBundle {
    public SQLConnection sqlConnection;
    public ServerExecutionBundle(Instruction instruction, ServerConnectionHandler serverConnectionHandler, SQLConnection sqlConnection) {
        super(instruction, serverConnectionHandler);
        this.sqlConnection = sqlConnection;
    }
}
