package org.client.app;

import org.chat.Message;
import org.client.ClientNotLoggedInException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ChatPanel extends JPanel {
    private ClientApp clientApp;
    private JPanel mainPanel;
    private JButton sendButton;
    private JTextField messageField;
    private JScrollPane messageScrollPane;
    private JPanel messagePanel;

    public ChatPanel(ClientApp clientApp) {
        this.clientApp = clientApp;

        $$$setupUI$$$();
        add(mainPanel);
    }

    public void loadMessages(String chat) {
        messagePanel.removeAll();

        Message[] messages;
        try {
            messages = clientApp.getClient().getMessages(chat, Integer.MAX_VALUE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClientNotLoggedInException e) {
            throw new RuntimeException(e);
        }

        for (Message m : messages) {
            MessagePanel mPanel = new MessagePanel(m.getMessage(), m.getUsername(), m.getDate().toString());

            messagePanel.add(mPanel);
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setMinimumSize(new Dimension(57, 57));
        mainPanel.setPreferredSize(new Dimension(57, 57));
        messageScrollPane = new JScrollPane();
        messageScrollPane.setPreferredSize(new Dimension(14, 350));
        messageScrollPane.setVerticalScrollBarPolicy(22);
        mainPanel.add(messageScrollPane, BorderLayout.CENTER);
        messageScrollPane.setViewportView(messagePanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMinimumSize(new Dimension(57, 57));
        panel1.setPreferredSize(new Dimension(57, 57));
        mainPanel.add(panel1, BorderLayout.SOUTH);
        sendButton = new JButton();
        sendButton.setText("send");
        panel1.add(sendButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        messageField = new JTextField();
        panel1.add(messageField, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
    }
}
