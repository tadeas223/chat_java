package org.server;

import org.connection.ConnectionHandler;
import org.protocol.Instruction;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.InstructionExecutor;
import org.protocol.protocolHandling.MissingDefaultException;
import org.server.executables.*;

import java.io.IOException;
import java.sql.SQLException;

public class ServerExecutor extends InstructionExecutor {
    private final SQLConnection sqlConnection = new SQLConnection();
    public ServerExecutor(){
        try {
            sqlConnection.connect();
        } catch (SQLException e) {
            // IDK
            throw new RuntimeException(e);
        }

        instMethodList.put("DEFAULT", new DEFAULT());

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

    }

    @Override
    public synchronized void execute(Instruction instruction, ConnectionHandler connectionHandler) throws IOException, MissingDefaultException {
        Executable executable = instMethodList.get(instruction.getName());

        ServerExecutionBundle executionBundle = new ServerExecutionBundle(instruction, (ServerConnectionHandler) connectionHandler, sqlConnection);

        executeExecutable(executable,executionBundle);
    }
}
