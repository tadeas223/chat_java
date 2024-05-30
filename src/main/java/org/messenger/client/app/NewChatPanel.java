package org.messenger.client.app;

import org.messenger.client.Client;
import org.messenger.protocol.ChatProtocolException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class NewChatPanel extends JPanel {
    private ClientApp clientApp;

    private JPanel mainPanel;
    private JTextField chatField;
    private JButton addButton;
    private JButton backButton;

    public NewChatPanel(ClientApp clientApp) {
        this.clientApp = clientApp;

        add(mainPanel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = clientApp.getClient();

                try {
                    if (client.userExists(chatField.getText())) {
                        client.addChat(chatField.getText());
                    }

                    clientApp.setCard("appPanel");
                } catch (IOException | ChatProtocolException | SQLException ex) {
                    clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientApp.setCard("appPanel");
            }
        });
    }

}
