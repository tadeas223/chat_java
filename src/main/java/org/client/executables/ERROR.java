package org.client.executables;

import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * This executable handles any errors thrown by the server.
 */
public class ERROR implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        // Do nothing for now
    }
}
