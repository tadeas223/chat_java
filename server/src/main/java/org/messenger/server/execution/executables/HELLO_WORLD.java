package org.messenger.server.execution.executables;

import org.messenger.protocol.InstructionBuilder;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;

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
