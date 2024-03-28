package org.protocol;

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
