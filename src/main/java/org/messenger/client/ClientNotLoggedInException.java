package org.messenger.client;

public class ClientNotLoggedInException extends Exception{
    public ClientNotLoggedInException() {
    }

    public ClientNotLoggedInException(String message) {
        super(message);
    }
}
