package org.messenger.client.executables;

import org.messenger.client.socketData.MessageListenerData;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

public class INIT implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        executionBundle.connectionHandler.addData(new MessageListenerData());
    }
}
