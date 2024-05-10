package org.server.executables;

import org.protocol.InstructionBuilder;
import org.protocol.protocolHandling.Executable;
import org.protocol.protocolHandling.ExecutionBundle;

import java.io.IOException;

/**
 * This executable is used for debugging. It returns an OUTPUT instruction with a inputted message.
 */
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
