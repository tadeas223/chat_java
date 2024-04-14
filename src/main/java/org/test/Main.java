package org.test;

import org.client.Client;
import org.protocol.ChatProtocolException;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, ChatProtocolException, SQLException {

        Client client = new Client();

        System.out.println("connected to server");

        client.login("test1","secretpassword");

        client.sendMessage("hello from user 1", "test2");

        System.out.println("waiting for incoming messages...");

    }
}
