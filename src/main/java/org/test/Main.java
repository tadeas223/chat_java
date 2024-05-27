package org.test;

import org.client.Client;
import org.protocol.ChatProtocolException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This class is user for testing, it will be deleted later
 */
public class Main {
    public static void main(String[] args) throws IOException, ChatProtocolException, SQLException {

        Client client = new Client("localhost");

        client.loginWithoutEncryption("test2","test2");

//        client.saveToDatabase("test2","hello");
//        client.saveToDatabase("test2","hello");

        client.getFromDatabase();
    }
}
