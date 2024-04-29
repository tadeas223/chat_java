package org.client;

import org.client.executables.DEFAULT;
import org.client.executables.ERROR;
import org.client.executables.MESSAGE;
import org.protocol.protocolHandling.InstructionExecutor;

/**
 * This is an executor for the client.
 * It has instruction executables for handling the client connection.
 */
public class ClientExecutor extends InstructionExecutor {
    public ClientExecutor(){
        instMethodList.put("DEFAULT", new DEFAULT());

        instMethodList.put("MESSAGE",new MESSAGE());
        instMethodList.put("ERROR", new ERROR());
    }
}
