package org.security;

import org.junit.jupiter.api.Test;
import org.messenger.security.SHA256;

import static org.junit.jupiter.api.Assertions.*;

class SHA256Test {

    @Test
    void encode() {
        String text1 = "hello";
        String text2 = "hello2";

        String good1 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
        String good2 = "87298cc2f31fba73181ea2a9e6ef10dce21ed95e98bdac9c4e1504ea16f486e4";

        String result1 = SHA256.encode(text1);
        String result2 = SHA256.encode(text2);

        assertEquals(good1,result1);
        assertEquals(good2,result2);
    }
}