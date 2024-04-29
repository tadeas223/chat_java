package org.client;

import org.chat.Message;
import org.connection.MsgReadListener;
import org.connection.SocketConnection;
import org.protocol.*;
import org.security.SHA256;

import java.io.IOException;
import java.sql.SQLException;
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
    public Client() throws IOException, SQLException {
        // Connecting to the server
        this.socketConnection = new SocketConnection("localhost", SocketConnection.SERVER_PORT);
        clientConnectionHandler = new ClientConnectionHandler(this);

        // Starting the listening thread
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
        // Hashing the password
        password = SHA256.encode(password);

        // Sending a login instruction to the server
        socketConnection.writeInstruction(InstructionBuilder.login(username, password));

        // Waiting for response
        Instruction instruction = stringToInst(waitForMessage());

        // Throw an Exception when the server returns an error
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        // Save the username
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
        // Hash the password
        password = SHA256.encode(password);

        // Sending a signup instruction to the server
        socketConnection.writeInstruction(InstructionBuilder.signup(username, password));

        // Waiting for response
        Instruction instruction = stringToInst(waitForMessage());

        // Throw an Exception when the server returns an error
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        // Save the username
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
        // Sending the signup instruction to the server
        socketConnection.writeInstruction(InstructionBuilder.signup(username, password));

        // Waiting for response
        Instruction instruction = stringToInst(waitForMessage());

        // Throw an Exception when the server throws an error
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        // Save the username
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
        // Sending the login instruction to the server
        socketConnection.writeInstruction(InstructionBuilder.login(username, password));

        // Waiting for response
        Instruction instruction = stringToInst(waitForMessage());

        // Throw an Exception when the server throws an error
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        // Save the username
        this.username = username;
    }

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
        // Sending the is online instruction to the server
        socketConnection.writeInstruction(InstructionBuilder.isOnline(username));

        // Waiting for response
        Instruction instruction = stringToInst(waitForMessage());

        // Return the response or throw Exception when an error is received
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
        // Sending the send message instruction to the server
        socketConnection.writeInstruction(InstructionBuilder.sendMessage(message, username));

        // Waiting for response
        Instruction instruction = stringToInst(waitForMessage());

        // Throw an Exception when an error is received
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        // Connect to a local database
        MessageDB messageDB = new MessageDB();
        messageDB.connect();
        if (!messageDB.containsChat(username)) {
            messageDB.createChat(username);
        }

        // Create a message object
        Message msg = new Message(this.username, message);

        // Save message to the DB
        messageDB.addMessage(msg, username);

        messageDB.close();
    }

    /**
     * Waits until the next message from the server is sent.
     * @return the message that the server sent
     */
    public String waitForMessage() {
        // Reset the message
        message = null;

        // Until the message is the same busy wait
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
     * Converts the string into an instruction.
     * When the instruction is invalid, it sends an error to the server.
     * @param string to be converted
     * @return the converted instruction
     * @throws IOException when an I/O error occurs when sending the error message
     */
    public Instruction stringToInst(String string) throws IOException {
        try {
            // Convert the string into a instruction
            return ProtocolTranslator.decode(string);
        } catch (InvalidStringException e) {
            // Send an error to the server
            socketConnection.writeInstruction(InstructionBuilder.error("Invalid instruction"));

            // Return a non-null instruction
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
