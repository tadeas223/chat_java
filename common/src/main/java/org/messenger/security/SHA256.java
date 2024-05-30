package org.messenger.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class used to encode text by SHA-256 hashing algorithm.
 * This class is entirely copied from <a href="https://www.baeldung.com/sha-256-hashing-java">here</a>
 */
public class SHA256 {
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Encodes the string by the SHA-256 algorithm.
     *
     * @param string original text
     * @return encoded text
     */
    public static String encode(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e + " - this exception should never happened. If you see this then something very horrible happened :-(");
        }
    }
}
