package org.messenger.client.app;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel {
    private JPanel mainPanel;
    private JLabel messageLabel;
    private JLabel usernameLabel;
    private JLabel dateLabel;

    public MessagePanel(String message, String username, String date) {
        messageLabel.setText(message);
        usernameLabel.setText(username);
        dateLabel.setText(date);

        add(mainPanel);
    }

    public String getMessage() {
        return messageLabel.getText();
    }

    public String getUsername() {
        return usernameLabel.getText();
    }

    public String getDate() {
        return dateLabel.getText();
    }

}
