package org.messenger.client.app;

import org.messenger.client.Client;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class ServerConnectPanel extends JPanel {
    private ClientApp clientApp;
    private JButton connectButton;
    private JPanel mainPanel;
    private JTextField ipField;
    private JTextField portField;

    public ServerConnectPanel(ClientApp clientApp) {
        this.add(mainPanel);
        this.clientApp = clientApp;

        connectButton.addActionListener(e -> {
            try {
                if (portField.getText().equals("")) {
                    this.clientApp.setClient(new Client(ipField.getText()));
                } else {
                    this.clientApp.setClient(new Client(ipField.getText(), Integer.parseInt(portField.getText())));
                }

                clientApp.setCard("loginPanel");
            } catch (IOException | SQLException ex) {
                clientApp.getErrLabel().setText("Error: " + ex.getMessage());
            }
        });
    }

}
