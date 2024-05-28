package org.client.app;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContactsPanel extends JPanel {
    private final ClientApp clientApp;
    private final AppPanel appPanel;
    private JPanel mainPanel;
    private JPanel contactsHoldPanel;
    private JLabel contactsLabel;
    private JButton addContactBut;

    public ContactsPanel(ClientApp clientApp, AppPanel appPanel) {
        this.clientApp = clientApp;
        this.appPanel = appPanel;

        add(mainPanel);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        contactsHoldPanel = new JPanel();
        contactsHoldPanel.setLayout(new BoxLayout(contactsHoldPanel, BoxLayout.Y_AXIS));


    }

    public void addContact(String name) {
        JButton button = new JButton(name);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appPanel.showChat(name);
            }
        });
    }

    public void resetContacts() {
        contactsHoldPanel.removeAll();
    }

}
