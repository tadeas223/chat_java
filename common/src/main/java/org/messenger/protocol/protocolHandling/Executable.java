package org.messenger.protocol.protocolHandling;

import java.io.IOException;

public interface Executable {
    void execute(ExecutionBundle executionBundle) throws IOException;
}
