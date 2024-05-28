package org.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolTranslatorTest {

    @Test
    void encode() {
        Instruction result1 = new Instruction("HELLO");
        Instruction result2 = new Instruction("HELLO",new ParamList("key","value"));
        ParamList paramList = new ParamList("key1","value1");
        paramList.put("key2","value2");
        Instruction result3 = new Instruction("HELLO", paramList);

        assertEquals(ProtocolTranslator.encode(result1),"HELLO{}");
        assertEquals(ProtocolTranslator.encode(result2),"HELLO{key=value;}");
        assertEquals(ProtocolTranslator.encode(result3),"HELLO{key1=value1;key2=value2;}");
    }

    @Test
    void decode() {
        Instruction result1 = new Instruction("HELLO");
        Instruction result2 = new Instruction("HELLO",new ParamList("key","value"));
        ParamList paramList = new ParamList("key1","value1");
        paramList.put("key2","value2");
        Instruction result3 = new Instruction("HELLO", paramList);

        try{
            assertEquals(ProtocolTranslator.decode("HELLO"),result1);
            assertEquals(ProtocolTranslator.decode("HELLO{key=value;}"),result2);
            assertEquals(ProtocolTranslator.decode("HELLO{key1=value1;key2=value2;}"),result3);
        } catch (InvalidStringException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void checkValidity() {
        assertFalse(ProtocolTranslator.checkValidity("HELLO{k=;ey=value}"));
        assertFalse(ProtocolTranslator.checkValidity("HEL LO{key=value}"));
        assertFalse(ProtocolTranslator.checkValidity("HELLO{key=value}asdfadfsaf"));

        assertTrue(ProtocolTranslator.checkValidity("HELLO"));
        assertTrue(ProtocolTranslator.checkValidity("HELLO{}"));
        assertTrue(ProtocolTranslator.checkValidity("HELLO{key=value;}"));
    }
}