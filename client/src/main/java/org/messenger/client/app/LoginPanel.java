package org.messenger.client.app;

import org.messenger.protocol.ChatProtocolException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginPanel extends JPanel {
    private ClientApp clientApp;
    private JPanel mainPanel;
    private JButton signUpButton;
    private JButton loginButton;
    private JTextField textField;
    private JPasswordField passwordField;
    public LoginPanel(ClientApp clientApp) {
        this.clientApp = clientApp;

        add(mainPanel);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clientApp.getClient().login(textField.getText(), Arrays.toString(passwordField.getPassword()));

                    clientApp.getClient().getFromDatabase();

                    clientApp.setCard("appPanel");
                } catch (IOException | ChatProtocolException | SQLException ex) {
                    clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                }
            }
        });
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientApp.setCard("signUpPanel");
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
