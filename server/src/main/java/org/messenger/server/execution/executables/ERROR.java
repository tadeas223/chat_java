package org.messenger.server.execution.executables;

import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * Executables that handles errors.
 */
public class ERROR implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        // Do nothing for now
    }
}
