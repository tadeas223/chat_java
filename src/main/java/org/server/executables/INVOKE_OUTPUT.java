package org.server.executables;

import org.protocol.InstructionBuilder;
import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import java.io.IOException;

public class INVOKE_OUTPUT implements Executable {

    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        String message = executionBundle.instruction.getParam("message");
        if(message == null){
            executionBundle.connection.writeInstruction(InstructionBuilder.error("Missing argument"));
            return;
        }

        executionBundle.connection.writeInstruction(InstructionBuilder.output(message));
    }
}
