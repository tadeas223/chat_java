package org.client;

import org.client.executables.DEFAULT;
import org.client.executables.ERROR;
import org.client.executables.MESSAGE;
import org.protocol.protocolHandling.InstructionExecutor;

public class ClientExecutor extends InstructionExecutor {
    public ClientExecutor(){
        instMethodList.put("DEFAULT", new DEFAULT());

        instMethodList.put("MESSAGE",new MESSAGE());
        instMethodList.put("ERROR", new ERROR());
    }
}
