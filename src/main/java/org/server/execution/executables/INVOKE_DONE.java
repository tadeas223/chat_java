package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * This executable returns DONE instruction.
 */
public class INVOKE_DONE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connection.writeInstruction(InstructionBuilder.done());
    }
}
