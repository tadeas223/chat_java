package org.client.app;

import org.chat.Message;
import org.client.Client;
import org.client.ClientNotLoggedInException;
import org.protocol.ChatProtocolException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class AppPanel extends JPanel {
    private ClientApp clientApp;
    private JPanel mainPanel;
    private JButton newChatBut;
    private JButton sendBut;
    private JTextField msgField;
    private JScrollPane contactsPane;
    private JScrollPane messagePane;
    private JLabel chatLabel;
    private JCheckBox DBSaveCheckBox;
    private JButton logoutButton;
    private JPanel contactsHolder;
    private JPanel messageHolder;

    private String currentChat;

    public AppPanel(ClientApp clientApp) {
        this.clientApp = clientApp;

        add(mainPanel);
        newChatBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientApp.setCard("newChatPanel");
            }
        });

        sendBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentChat != null) {
                    String message = msgField.getText();

                    try {
                        clientApp.getClient().sendMessage(message, currentChat);
                    } catch (IOException | ChatProtocolException | SQLException ex) {
                        clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                    }


                    loadContacts();
                    showChat(currentChat);

                    clientApp.redraw();
                    resetScrollBar();
                }
            }
        });
        DBSaveCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();

                try {
                    clientApp.getClient().autoSave(source.isSelected());
                } catch (IOException | ChatProtocolException ex) {
                    clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                }
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clientApp.getClient().logout();

                    clientApp.setCard("loginPanel");
                } catch (IOException | ChatProtocolException ex) {
                    clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                }
            }
        });
    }

    public void loadContacts() {
        contactsHolder.removeAll();
        String[] chats = new String[0];
        try {
            chats = clientApp.getClient().getChats();
        } catch (ClientNotLoggedInException | SQLException e) {
            clientApp.getErrLabel().setText("Error: " + e.getMessage());
        }

        for (String chat : chats) {
            JButton but = new JButton(chat);

            but.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showChat(chat);
                    resetScrollBar();
                }
            });

            contactsHolder.add(but);
        }

        clientApp.redraw();
    }

    public void showChat(String chat) {
        if (chat == null) return;

        messageHolder.removeAll();

        chatLabel.setText(chat);
        currentChat = chat;

        Client client = clientApp.getClient();
        Message[] messages = new Message[0];
        try {
            messages = client.getMessages(chat, Integer.MAX_VALUE);
        } catch (ClientNotLoggedInException | SQLException e) {
            clientApp.getErrLabel().setText("Error: " + e.getMessage());
        }


        for (Message msg : messages) {
            JLabel label = new JLabel(msg.getMessage() +
                    "  |  " + msg.getUsername() +
                    "  |  " + msg.getDate().toString());

            messageHolder.add(label, 0);
            clientApp.redraw();
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        contactsHolder = new JPanel();
        contactsHolder.setLayout(new BoxLayout(contactsHolder, BoxLayout.Y_AXIS));

        messageHolder = new JPanel();
        messageHolder.setLayout(new BoxLayout(messageHolder, BoxLayout.Y_AXIS));

        contactsPane = new JScrollPane(contactsHolder);
        messagePane = new JScrollPane(messageHolder);
    }

    public void reset() {
        loadContacts();
        showChat(currentChat);
        resetScrollBar();
    }

    public void resetScrollBar() {
        messagePane.getVerticalScrollBar().setValue(messagePane.getVerticalScrollBar().getMaximum());
    }
}
