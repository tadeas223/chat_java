package org.messenger.client;

import org.messenger.client.executables.DEFAULT;
import org.messenger.client.executables.ERROR;
import org.messenger.client.executables.INIT;
import org.messenger.client.executables.MESSAGE;
import org.messenger.protocol.protocolHandling.InstructionExecutor;

/**
 * This is an executor for the client.
 * It has instruction executables for handling the client connection.
 */
public class ClientExecutor extends InstructionExecutor {
    public ClientExecutor(){
        instMethodList.put("DEFAULT", new DEFAULT());
        instMethodList.put("INIT", new INIT());

        instMethodList.put("MESSAGE",new MESSAGE());
        instMethodList.put("ERROR", new ERROR());
    }
}
