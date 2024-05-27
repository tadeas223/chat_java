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
//        client.signup("test","test");

        client.login("test2","tes2");

        client.autoSave(true);

        client.sendMessage("hello auto save","test");

        client.close();
    }
}
