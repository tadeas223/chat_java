package org.messenger.protocol;

import java.util.Objects;

/**
 * This class can be used for communication between two programs.
 * This class can be entirely converted into {@link String} and back by the {@link ProtocolTranslator}.
 * Because of this, it can be passed into a {@link java.io.OutputStream} enabling it to work as a communication medium.
 */
public class Instruction  {
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getParamList(), that.getParamList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParamList());
    }

    private ParamList paramList = new ParamList();

    /**
     * Creates a new instruction with the name and paramList.
     *
     * @param name      of the instruction
     * @param paramList parameters of the instruction
     */
    public Instruction(String name, ParamList paramList) {
        this.name = name;
        this.paramList = paramList;
    }

    /**
     * Creates a new instruction with the name and does not add amy parameters.
     *
     * @param name of the instruction
     */
    public Instruction(String name) {
        this.name = name;
    }

    /**
     * Copies all parameters and the name from the passed instruction
     *
     * @param instruction that should be copied
     */
    public Instruction(Instruction instruction) {
        paramList = instruction.getParamList();
        name = instruction.getName();
    }

    public ParamList getParamList() {
        return paramList;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a value corresponding to the key passed.
     *
     * @param key of the value
     * @return the value or null of the paramList does not contain the key
     */
    public String getParam(String key) {
        return paramList.get(key);
    }

    /**
     * Translates the instruction by {@link ProtocolTranslator}.
     * This string can be then decoded back into instruction.
     *
     * @return instruction in the text form
     */
    @Override
    public String toString() {
        return ProtocolTranslator.encode(this);
    }
}
