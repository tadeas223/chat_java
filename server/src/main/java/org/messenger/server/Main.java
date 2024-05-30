package org.messenger.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("data/server/");

        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            server.close();
        }
    }
}
