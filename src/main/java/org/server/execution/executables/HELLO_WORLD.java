package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * This executable returns HELLO_WORLD instruction.
 */
public class HELLO_WORLD implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connection.writeInstruction(InstructionBuilder.helloWorld());
    }
}
