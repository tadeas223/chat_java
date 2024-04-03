package org.client.executables;

import org.protocolHandling.Executable;
import org.protocolHandling.ExecutionBundle;

import java.io.IOException;

public class MESSAGE implements Executable {
    @Override
    public void execute(ExecutionBundle executionBundle) throws IOException {
        System.out.println(executionBundle.instruction.getParam("message"));
    }
}
