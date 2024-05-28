package org.client.app;

import org.client.Client;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

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
