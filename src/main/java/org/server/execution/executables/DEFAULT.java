package org.server.execution.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * Default executable that is called when the instruction from client does not have an executable.
 */
public class DEFAULT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connection.writeInstruction(InstructionBuilder.error("Unknown instruction"));
    }
}
