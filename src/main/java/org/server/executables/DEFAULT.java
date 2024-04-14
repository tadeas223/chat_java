package org.server.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

public class DEFAULT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connection.writeInstruction(InstructionBuilder.error("Unknown instruction"));
    }
}
