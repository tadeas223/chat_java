package org.client.app;

import org.client.Client;
import org.client.socketData.MessageListener;
import org.client.socketData.MessageListenerData;
import org.protocol.ChatProtocolException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class ClientApp extends JFrame {
    private Client client;
    private JPanel mainPanel;
    private JPanel serverConnectPanel;
    private JPanel loginPanel;
    private JPanel signUpPanel;
    private JPanel appPanel;
    private JPanel newChatPanel;
    private JPanel cardPanel;
    private JLabel errLabel;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        serverConnectPanel = new ServerConnectPanel(this);
        serverConnectPanel.setLayout(new GridLayout(1, 1));

        signUpPanel = new SignUpPanel(this);
        signUpPanel.setLayout(new GridLayout(1, 1));

        loginPanel = new LoginPanel(this);
        loginPanel.setLayout(new GridLayout(1, 1));

        appPanel = new AppPanel(this);
        appPanel.setLayout(new GridLayout(1, 1));

        newChatPanel = new NewChatPanel(this);
        newChatPanel.setLayout(new GridLayout(1, 1));

    }

    public static void main(String[] args) {
        ClientApp clientApp = new ClientApp();
        clientApp.getContentPane().add(clientApp.mainPanel, BorderLayout.CENTER);
        clientApp.setCard("serverConnectPanel");
        clientApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientApp.setSize(500, 500);
        clientApp.setVisible(true);
    }

    public void setCard(String card) {
        if (card.equals("appPanel")) {
            ((AppPanel) appPanel).reset();
        }

        ((CardLayout) cardPanel.getLayout()).show(cardPanel, card);
        errLabel.setText(".");
    }

    public void nextCard() {
        ((CardLayout) cardPanel.getLayout()).next(cardPanel);
        errLabel.setText(".");
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        client.getClientConnectionHandler()
                .getData(MessageListenerData.class)
                .addListener(new MessageListener() {
                    @Override
                    public void messageReceived() {
                        ((AppPanel) appPanel).reset();
                    }
                });
        this.client = client;
    }

    public void redraw() {
        revalidate();
        repaint();
    }

    public JLabel getErrLabel() {
        return errLabel;
    }
}
