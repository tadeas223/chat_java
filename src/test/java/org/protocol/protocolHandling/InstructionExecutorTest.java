package org.protocol.protocolHandling;

import org.client.ClientConnectionHandler;
import org.connection.ConnectionHandler;
import org.junit.jupiter.api.Test;
import org.protocol.Instruction;

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