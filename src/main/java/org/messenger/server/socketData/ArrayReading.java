package org.messenger.server.socketData;

import org.messenger.connection.socketData.SocketData;
import org.messenger.protocol.Instruction;

import java.util.Iterator;

public class ArrayReading implements SocketData, Iterator<Instruction> {
    private final Instruction[] instructions;
    private int currentInst;

    public ArrayReading(Instruction[] instructions) {
        this.instructions = instructions;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public int getCurrentInst() {
        return currentInst;
    }

    public Instruction get(int index){
        return instructions[index];
    }

    @Override
    public boolean hasNext() {
        return currentInst < instructions.length;
    }

    @Override
    public Instruction next() {
        Instruction instruction =  instructions[currentInst];
        currentInst++;
        return instruction;
    }

}
