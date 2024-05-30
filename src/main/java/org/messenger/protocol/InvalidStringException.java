package org.messenger.protocol;


/**
 * This exception is used when a {@link String} doesn't match an expected pattern.
 */
public class InvalidStringException extends ChatProtocolException {
    public InvalidStringException() {
    }

    public InvalidStringException(String message) {
        super(message);
    }

    public InvalidStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStringException(Throwable cause) {
        super(cause);
    }

    public InvalidStringException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
