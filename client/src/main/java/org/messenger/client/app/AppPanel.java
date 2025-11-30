package org.messenger.client.app;

import jdk.swing.interop.SwingInterOpUtils;
import org.messenger.chat.ChatMedia;
import org.messenger.chat.File;
import org.messenger.chat.Message;
import org.messenger.client.Client;
import org.messenger.client.ClientNotLoggedInException;
import org.messenger.fileCreation.FileCreator;
import org.messenger.protocol.ChatProtocolException;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

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
    private JButton sendFileBut;
    private JPanel contactsHolder;
    private JPanel messageHolder;

    private String currentChat;

    public AppPanel(ClientApp clientApp) {
        this.clientApp = clientApp;

        $$$setupUI$$$();
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

                    CompletableFuture<Void> sendMessageFuture = clientApp.getClient().sendMessageFuture(message, currentChat);
                    sendMessageFuture.thenAccept((Void v) -> {
                        SwingUtilities.invokeLater(() -> {
                            loadContacts();
                            showChat(currentChat);

                            clientApp.redraw();
                            reset();
                        });
                    }).exceptionally((ex) -> {
                        SwingUtilities.invokeLater(() -> {
                            clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                        });
                        return null;
                    });

                    //resetScrollBar();
                }
            }
        });

        sendFileBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentChat != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    int result = fileChooser.showOpenDialog(null);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        java.io.File file = fileChooser.getSelectedFile();
                        CompletableFuture<Void> sendFileFuture = clientApp.getClient().sendFileFuture(file.getAbsolutePath(), currentChat);
                        sendFileFuture.thenAccept((v) -> SwingUtilities.invokeLater(() -> reset())).exceptionally((ex) -> {
                            SwingUtilities.invokeLater(() -> {
                                clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                            });
                            return null;
                        });
                    }
                }
            }
        });

        DBSaveCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();

                boolean isSelected = source.isSelected();
                CompletableFuture<Void> autoSaveFuture = clientApp.getClient().autoSaveFuture(isSelected);
                autoSaveFuture.exceptionally((ex) -> {
                    SwingUtilities.invokeLater(() -> clientApp.getErrLabel().setText("Error: " + ex.getMessage()));
                    return null;
                });
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompletableFuture<Void> logoutFuture = clientApp.getClient().logoutFuture();
                logoutFuture
                    .thenAccept((Void v) -> {
                        SwingUtilities.invokeLater(() -> {
                            clientApp.setCard("loginPanel");
                        });
                    })
                    .exceptionally((ex) -> {
                        clientApp.getErrLabel().setText("Error: " + ex.getMessage());
                        return null;
                    });
            }
        });


        msgField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if (str == null) return;

                if ((getLength() + str.length()) <= 50) {
                    super.insertString(offset, str, attr);
                } else {
                    //SwingUtilities.invokeLater(() -> clientApp.getErrLabel().setText("Error: maximum message length is 50"));
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
        ChatMedia[] messages = new Message[0];
        try {
            messages = client.getMessages(chat, Integer.MAX_VALUE);
        } catch (ClientNotLoggedInException | SQLException e) {
            clientApp.getErrLabel().setText("Error: " + e.getMessage());
        }


        for (ChatMedia msg : messages) {
            JLabel label = null;
            if (msg instanceof Message cast) {
                label = new JLabel(cast.getMessage() +
                        "  |  " + cast.getUsername() +
                        "  |  " + cast.getDate().toString());
            } else if (msg instanceof File cast) {
                label = new JLabel("<html><a href=''>" + cast.getFileName() + "</a></html>" +
                        "  |  " + cast.getUsername() +
                        "  |  " + cast.getDate().toString());
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int result = chooser.showOpenDialog(null);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            java.io.File targetDir = chooser.getSelectedFile();
                            java.io.File file = new java.io.File(targetDir, cast.getFileName());
                            try {
                                System.out.println(Path.of(client
                                        .getFileCreator()
                                        .getAbsolutePath(
                                                Path.of(client.getUser().getUsername()).resolve(chat).resolve(cast.getFileName()).toString()
                                        )).toString());
                                Files.copy(Path.of(client
                                        .getFileCreator()
                                        .getAbsolutePath(
                                                Path.of(client.getUser().getUsername()).resolve(chat).resolve(cast.getFileName()).toString()
                                        )), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                JOptionPane.showMessageDialog(null, "File copied to: " + file.getAbsolutePath());
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "Error copying file: " + ex.getMessage());
                            }
                        }
                    }
                });

            }
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
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        mainPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newChatBut = new JButton();
        newChatBut.setText("new chat");
        panel1.add(newChatBut, BorderLayout.SOUTH);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 16, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setText("contacts");
        panel1.add(label1, BorderLayout.NORTH);
        panel1.add(contactsPane, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        mainPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, BorderLayout.SOUTH);
        sendBut = new JButton();
        sendBut.setText("send");
        panel3.add(sendBut, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        msgField = new JTextField();
        panel3.add(msgField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        DBSaveCheckBox = new JCheckBox();
        DBSaveCheckBox.setText("DB save");
        panel3.add(DBSaveCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendFileBut = new JButton();
        sendFileBut.setText("send file");
        panel3.add(sendFileBut, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        messagePane.setOpaque(false);
        panel2.add(messagePane, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        Font panel4Font = this.$$$getFont$$$(null, -1, 22, panel4.getFont());
        if (panel4Font != null) panel4.setFont(panel4Font);
        mainPanel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        chatLabel = new JLabel();
        Font chatLabelFont = this.$$$getFont$$$(null, -1, 22, chatLabel.getFont());
        if (chatLabelFont != null) chatLabel.setFont(chatLabelFont);
        chatLabel.setHorizontalAlignment(0);
        chatLabel.setHorizontalTextPosition(0);
        chatLabel.setText("chat");
        panel4.add(chatLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoutButton = new JButton();
        logoutButton.setText("logout");
        mainPanel.add(logoutButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
