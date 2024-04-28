package org.client.executables;

import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

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
