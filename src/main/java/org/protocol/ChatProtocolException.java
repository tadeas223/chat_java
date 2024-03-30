package org.protocol;

/**
 * This exception is used to convert instruction ERROR messages into a throwable object.
 * It is also used to indicate any problems with the instruction based communication.
 */
public class ChatProtocolException extends Exception {
    public ChatProtocolException() {
    }

    public ChatProtocolException(String message) {
        super(message);
    }

    public ChatProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatProtocolException(Throwable cause) {
        super(cause);
    }

    public ChatProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
