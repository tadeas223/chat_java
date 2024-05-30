package org.protocol.protocolHandling;

import org.messenger.connection.ConnectionHandler;
import org.junit.jupiter.api.Test;
import org.messenger.protocol.Instruction;
import org.messenger.protocol.protocolHandling.Executable;
import org.messenger.protocol.protocolHandling.ExecutionBundle;
import org.messenger.protocol.protocolHandling.InstructionExecutor;
import org.messenger.protocol.protocolHandling.MissingDefaultException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InstructionExecutorTest {

    @Test
    void execute() {
        InstructionExecutor instructionExecutor = new InstructionExecutor();

        assertThrows(MissingDefaultException.class,()->{
            instructionExecutor.execute(new Instruction("HELLO"),new ConnectionHandler());
        });

        Executable defaultExe = new Executable() {
            @Override
            public void execute(ExecutionBundle executionBundle) throws IOException {
                System.out.println("default");
            }
        };

        instructionExecutor.addExecutable("DEFAULT",defaultExe);

        assertDoesNotThrow(()->{
            instructionExecutor.execute(new Instruction("HELLO"),new ConnectionHandler());
        });
    }
}