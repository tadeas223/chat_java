package org.messenger.client.executables;

import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * This executable is called when the server sends to a client an instruction that doesn't have an executable.
 */
public class DEFAULT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        // Do nothing
    }
}
