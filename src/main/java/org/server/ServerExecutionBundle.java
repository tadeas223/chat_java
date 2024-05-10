package org.server;

import org.protocol.Instruction;
import org.protocol.protocolHandling.ExecutionBundle;

/**
 * This is class holds everything that the server needs to know to execute an instruction.
 */
public class ServerExecutionBundle extends ExecutionBundle {
    public SQLConnection sqlConnection;
    public ServerExecutionBundle(Instruction instruction, ServerConnectionHandler serverConnectionHandler, SQLConnection sqlConnection) {
        super(instruction, serverConnectionHandler);
        this.sqlConnection = sqlConnection;
    }
}
