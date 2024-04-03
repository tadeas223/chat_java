package org.protocolHandling;

public class MissingDefaultException extends Exception{
    public MissingDefaultException() {
    }

    public MissingDefaultException(String message) {
        super(message);
    }

    public MissingDefaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingDefaultException(Throwable cause) {
        super(cause);
    }

    public MissingDefaultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
