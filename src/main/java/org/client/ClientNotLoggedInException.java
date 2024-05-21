package org.client;

public class ClientNotLoggedInException extends Exception{
    public ClientNotLoggedInException() {
    }

    public ClientNotLoggedInException(String message) {
        super(message);
    }
}
