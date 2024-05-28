package org.client.executables;

import org.client.socketData.MessageListenerData;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

public class INIT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connectionHandler.addData(new MessageListenerData());
    }
}
