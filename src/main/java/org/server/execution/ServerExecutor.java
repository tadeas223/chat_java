package org.server.execution;

import org.connection.ConnectionHandler;
import org.protocol.Instruction;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.InstructionExecutor;
import org.protocol.protocolHandling.MissingDefaultException;
import org.server.SQLConnection;
import org.server.ServerConnectionHandler;
import org.server.execution.executables.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This class contains all valid instructions and the appropriate {@link Executable}.
 * This class is user with {@link ServerConnectionHandler}
 * to handle communication between the server and a client.
 * This class also creates a new {@link SQLConnection} session that is not closed anywhere :(.
 */
public class ServerExecutor extends InstructionExecutor {
    private final SQLConnection sqlConnection;

    public ServerExecutor() {
        try {
            sqlConnection = new SQLConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
            // IDK :(
        }

        instMethodList.put("DEFAULT", new DEFAULT());

        instMethodList.put("INIT", new INIT());

        instMethodList.put("HELLO_WORLD", new HELLO_WORLD());
        instMethodList.put("INVOKE_DONE", new INVOKE_DONE());
        instMethodList.put("INVOKE_OUTPUT", new INVOKE_OUTPUT());
        instMethodList.put("LOGIN", new LOGIN());
        instMethodList.put("SIGN_UP", new SIGN_UP());
        instMethodList.put("LOGOUT", new LOGOUT());
        instMethodList.put("GET_ID", new GET_ID());
        instMethodList.put("IS_ONLINE", new IS_ONLINE());
        instMethodList.put("SEND_MESSAGE", new SEND_MESSAGE());
        instMethodList.put("ERROR", new ERROR());
        instMethodList.put("SAVE_TO_DATABASE", new SAVE_TO_DATABASE());
        instMethodList.put("GET_FROM_DATABASE", new GET_FROM_DATABASE());
        instMethodList.put("AUTO_MESSAGE_SAVE", new AUTO_MESSAGE_SAVE());
        instMethodList.put("NEXT",new NEXT());
        instMethodList.put("GET",new GET());
        instMethodList.put("DONE",new DONE());

    }

    @Override
    public synchronized void execute(Instruction instruction, ConnectionHandler connectionHandler) throws IOException, MissingDefaultException {
        Executable executable = instMethodList.get(instruction.getName());

        ServerExecutionBundle executionBundle = new ServerExecutionBundle(instruction, (ServerConnectionHandler) connectionHandler, sqlConnection);

        executeExecutable(executable,executionBundle);
    }
}
