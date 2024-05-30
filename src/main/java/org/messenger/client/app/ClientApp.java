package org.messenger.client.app;

import org.messenger.client.Client;
import org.messenger.client.socketData.MessageListener;
import org.messenger.client.socketData.MessageListenerData;

import javax.swing.*;
import java.awt.*;

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

    public ClientApp(){
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

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
