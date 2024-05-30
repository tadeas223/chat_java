package org.messenger.server.execution;

import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.protocol.Instruction;
import org.messenger.server.SQLConnection;
import org.messenger.server.ServerConnectionHandler;

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
