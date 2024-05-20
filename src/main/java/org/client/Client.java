package org.client;

import org.chat.Message;
import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.*;
import org.security.SHA256;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Objects;

/**
 * This class is a code representation of the server-client connection from the client's perspective.
 * It has methods for easier use of this application.
 * When this class is instantiated it creates a new {@link Thread} for listening for incoming messages.
 * The Thread does not stop on its own - It needs to be stopped by the close() method.
 */
public class Client implements MsgReadListener {
    private final SocketConnection socketConnection;
    private final ClientConnectionHandler clientConnectionHandler;
    private String message = null;
    private String username;

    /**
     * Connects to the server at the default server port.
     * Starts a listening thread for server messages.
     * @throws IOException when in I/O error occurs when connection to the server
     * @throws SQLException when a message database fails to initialize
     */
    public Client(String ip) throws IOException, SQLException {
        this.socketConnection = new SocketConnection(ip, SocketConnection.SERVER_PORT);
        clientConnectionHandler = new ClientConnectionHandler(this);

        socketConnection.addMsgReadListener(clientConnectionHandler);
        socketConnection.addMsgReadListener(this);
        socketConnection.startReading();

        // Configuring the DB
        MessageDB messageDB = new MessageDB();
        messageDB.connect();
        if (!messageDB.containsChatsTable()) {
            messageDB.createChatsTable();
        }
        messageDB.close();
    }

    public Client(String ip, int port) throws IOException, SQLException {
        this.socketConnection = new SocketConnection(ip, port);
        clientConnectionHandler = new ClientConnectionHandler(this);

        socketConnection.addMsgReadListener(clientConnectionHandler);
        socketConnection.addMsgReadListener(this);
        socketConnection.startReading();

        // Configuring the DB
        MessageDB messageDB = new MessageDB();
        messageDB.connect();
        if (!messageDB.containsChatsTable()) {
            messageDB.createChatsTable();
        }
        messageDB.close();
    }

    /**
     * Logs in the user in the server.
     * If the user is not logged in,
     * most of the app features are not usable.
     * This method also hashed the inputted password.
     * If you don't want to have the password, use the loginWithoutEncryption() method instead.
     * @param username of the user
     * @param password of the user
     * @throws IOException when an I/O error occurs in the server communication
     * @throws ChatProtocolException when an error occurs with the message communication
     */
    public void login(String username, String password) throws IOException, ChatProtocolException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.login(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    /**
     * Registers and logs in the user to the app.
     * This method registers the user with the password hashed.
     * To register without hashing the password, use signupWithoutEncryption() method instead.
     * @param username of the user
     * @param password of the user
     * @throws IOException when an I/O error occurs in the server communication
     * @throws ChatProtocolException when an error occurs with the message communication
     */
    public void signup(String username, String password) throws IOException, ChatProtocolException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.signup(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    /**
     * Registers and logs in the user without hashing the password.
     * If use register by this method, then you can only log in by the loginWithoutEncryption() method.
     * @param username of the user
     * @param password of the user
     * @throws IOException when an I/O error occurs in the server communication
     * @throws ChatProtocolException when an error occurs with the message communication
     */
    public void signupWithoutEncryption(String username, String password) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.signup(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    /**
     * Logs in the user without hashing the password.
     * @param username of the user
     * @param password of the user
     * @throws ChatProtocolException when an error occurs with the message communication
     * @throws IOException when an I/O error occurs in the server communication
     */
    public void loginWithoutEncryption(String username, String password) throws ChatProtocolException, IOException {
        socketConnection.writeInstruction(InstructionBuilder.login(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        this.username = username;
    }

    /**
     * This method sends an INVOKE_DONE instruction and waits for a response.
     * @return true if the response returned, false if not
     * @throws IOException when an I/O error occurs when sending the instruction
     * @throws ChatProtocolException when an error occurs with the message communication
     */
    public boolean invokeDone() throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.invokeDone());

        Instruction instruction = stringToInst(waitForMessage());

        if(instruction.getName().equals("DONE")){
            return true;
        } else if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        } else{
            return false;
        }
    }

    /**
     * This method calls INVOKE_OUTPUT instruction and returns the response.
     * @param message in the output
     * @return the output message
     * @throws IOException when an I/O error occurs
     * @throws ChatProtocolException when an error occurs with the message communication
     */
    public String invokeOutput(String message) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.invokeOutput(message));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("OUTPUT")){
            return instruction.getParam("message");
        } else if(instruction.getName().equals("ERROR")){
            throw new ChatProtocolException(instruction.getParam("message"));
        } else {
            throw new ChatProtocolException("Unknown error");
        }

    }

    /**
     * This method is used to find if the user is logged into the server.
     * @param username that needs to be checked
     * @return true of the user is online, false if not
     * @throws IOException when an error occurs with the message communication
     * @throws ChatProtocolException when an I/O error occurs in the server communication
     */
    public boolean isOnline(String username) throws IOException, ChatProtocolException {
        socketConnection.writeInstruction(InstructionBuilder.isOnline(username));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("FALSE")) {
            return false;
        } else if (instruction.getName().equals("TRUE")) {
            return true;
        } else {
            throw new ChatProtocolException("Something went horribly wrong");
        }
    }

    /**
     * Sends the message to a user if the user is online.
     * It also saves the message to a {@link MessageDB}.
     * @param message that should be sent
     * @param username to which the message will be sent
     * @throws IOException when an I/O error occurs in the server communication
     * @throws ChatProtocolException when an error occurs with the message communication
     * @throws SQLException when the message fails to save to the database
     */
    public void sendMessage(String message, String username) throws IOException, ChatProtocolException, SQLException {
        socketConnection.writeInstruction(InstructionBuilder.sendMessage(message, username));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        MessageDB messageDB = new MessageDB();
        messageDB.connect();
        if (!messageDB.containsChat(username)) {
            messageDB.createChat(username);
        }

        Message msg = new Message(this.username, message);

        messageDB.addMessage(msg, username);

        messageDB.close();
    }

    /**
     * Waits until the next message from the server is sent.
     * @return the message that the server sent
     */
    public String waitForMessage() {
        message = null;

        while (Objects.equals(null, message)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // I don't care yet
            }
        }

        return message;
    }

    /**
     * This method is used to get all chats from the database
     * @return all chat names
     * @throws SQLException when an SQL error occurs
     */
    public String[] getChats() throws SQLException {
        MessageDB messageDB = new MessageDB();
        messageDB.connect();

        String[] chats = messageDB.getChats();

        messageDB.close();

        return chats;
    }

    /**
     * This method is used to get messages from a chat.
     * @param chat with the messages
     * @param count number of the messages that should be returned
     * @return the messages
     * @throws SQLException when a SQL error occurs
     */
    public Message[] getMessages(String chat, int count) throws SQLException {
        MessageDB messageDB = new MessageDB();
        messageDB.connect();

        Message[] messages = messageDB.getMessages(chat,count);

        messageDB.close();

        return messages;
    }

    /**
     * Converts the string into an instruction.
     * When the instruction is invalid, it sends an error to the server.
     * @param string to be converted
     * @return the converted instruction
     * @throws IOException when an I/O error occurs when sending the error message
     */
    public Instruction stringToInst(String string) throws IOException {
        try {
            return ProtocolTranslator.decode(string);
        } catch (InvalidStringException e) {
            socketConnection.writeInstruction(InstructionBuilder.error("Invalid instruction"));

            return new Instruction("PlS DoN'T UsE tHiS eXaCt TeXt FoR InStRuCtIoN");
        }
    }

    @Override
    public void messageRead(String msg) {
        message = msg;
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }

    /**
     * Closes the connection.
     * And also stop the listening thread.
     * This method needs to be called at the end of use of this object.
     * @throws IOException When the socket fails to close
     */
    public void close() throws IOException {
        socketConnection.close();
    }
}
