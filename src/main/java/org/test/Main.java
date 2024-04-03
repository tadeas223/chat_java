package org.test;

import org.client.Client;
import org.protocol.ChatProtocolException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ChatProtocolException {

        Client client = new Client();
        System.out.println("asd");

        client.login("test1","secretpassword");

        System.out.println("login");

        client.sendMessage("hello", "test2");

        System.out.println("send msg");

        System.out.println("msg sent");

    }
}
