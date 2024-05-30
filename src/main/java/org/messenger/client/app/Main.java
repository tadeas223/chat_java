package org.messenger.client.app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        ClientApp clientApp = new ClientApp();
        clientApp.setCard("serverConnectPanel");
        clientApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientApp.setSize(500, 500);
        clientApp.setVisible(true);
    }
}
