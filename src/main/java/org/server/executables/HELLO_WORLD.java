package org.server.executables;

import org.protocol.InstructionBuilder;
import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import java.io.IOException;

public class HELLO_WORLD implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connection.writeInstruction(InstructionBuilder.helloWorld());
    }
}
