package org.messenger.client;

import org.messenger.chat.ChatMedia;
import org.messenger.chat.File;
import org.messenger.chat.Message;
import org.messenger.client.app.Main;
import org.messenger.connection.socketData.AuthenticationData;
import org.messenger.connection.MsgReadListener;
import org.messenger.connection.SocketConnection;
import org.messenger.fileCreation.FileCreator;
import org.messenger.protocol.*;
import org.messenger.security.SHA256;
import org.messenger.security.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;

/**
 * This class is a code representation of the server-client connection from the client's perspective.
 * It has methods for easier use of this application.
 * When this class is instantiated it creates a new {@link Thread} for listening for incoming messages.
 * The Thread does not stop on its own - It needs to be stopped by the close() method.
 */
public class Client implements MsgReadListener {
    private final SocketConnection socketConnection;
    private final ClientConnectionHandler clientConnectionHandler;
    private final FileCreator fileCreator = new FileCreator(Main.class, "data/client/");
    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private String message = null;
    private final Object messageLock = new Object();
    private User user;


    /**
     * Connects to the server at the default server port.
     * Starts a listening thread for server messages.
     * @throws IOException when in I/O error occurs when connection to the server
     * @throws SQLException when a message database fails to initialize
     */
    public Client(String ip) throws IOException {
        this.socketConnection = new SocketConnection(ip, SocketConnection.SERVER_PORT);
        clientConnectionHandler = new ClientConnectionHandler(this);

        socketConnection.addMsgReadListener(clientConnectionHandler);
        socketConnection.addMsgReadListener(this);
        socketConnection.startReading();
    }

    public Client(String ip, int port) throws IOException, SQLException {
        this.socketConnection = new SocketConnection(ip, port);
        clientConnectionHandler = new ClientConnectionHandler(this);

        socketConnection.addMsgReadListener(clientConnectionHandler);
        socketConnection.addMsgReadListener(this);
        socketConnection.startReading();
    }

    public FileCreator getFileCreator() {
        return fileCreator;
    }

    private void configureDB() throws SQLException {
        MessageDB messageDB = new MessageDB(this);
        messageDB.connect(user.getUsername());
        if (!messageDB.containsChatsTable()) {
            messageDB.createChatsTable();
        }
        messageDB.close();
    }

    private MessageDB configureDBNoClose() throws SQLException {
        MessageDB messageDB = new MessageDB(this);
        messageDB.connect(user.getUsername());
        if (!messageDB.containsChatsTable()) {
            messageDB.createChatsTable();
        }
        return messageDB;
    }

    //region client_commands

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
    public void login(String username, String password) throws IOException, ChatProtocolException, SQLException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.login(username, password));
        Instruction instruction = stringToInst(waitForMessage());
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        socketConnection.writeInstruction(InstructionBuilder.getId());
        instruction = stringToInst(waitForMessage());
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        user = new User(username,Integer.parseInt(instruction.getParam("message")));

        AuthenticationData authenticationData = new AuthenticationData(user);
        clientConnectionHandler.addData(authenticationData);

        configureDB();
    }

    public CompletableFuture<Void> loginFuture(String username, String password) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                login(username, password);
            } catch (IOException | ChatProtocolException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
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
    public void signup(String username, String password) throws IOException, ChatProtocolException, SQLException {
        password = SHA256.encode(password);

        socketConnection.writeInstruction(InstructionBuilder.signup(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        socketConnection.writeInstruction(InstructionBuilder.getId());
        instruction = stringToInst(waitForMessage());
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        user = new User(username,Integer.parseInt(instruction.getParam("message")));

        configureDB();
    }

    public CompletableFuture<Void> signupFuture(String username, String password) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                signup(username, password);
            } catch (IOException | ChatProtocolException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public void logout() throws IOException, ChatProtocolException {
        Instruction instruction = InstructionBuilder.logout();

        socketConnection.writeInstruction(instruction);

        Instruction response = stringToInst(waitForMessage());

        if(response.getName().equals("ERROR")){
            throw new ChatProtocolException(response.getParam("message"));
        }
    }

    public CompletableFuture<Void> logoutFuture() {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                logout();
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    /**
     * Registers and logs in the user without hashing the password.
     * If a user registers by this method, then he can only log in by the loginWithoutEncryption() method.
     * @param username of the user
     * @param password of the user
     * @throws IOException when an I/O error occurs in the server communication
     * @throws ChatProtocolException when an error occurs with the message communication
     */
    public void signupWithoutEncryption(String username, String password) throws IOException, ChatProtocolException, SQLException {
        socketConnection.writeInstruction(InstructionBuilder.signup(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        socketConnection.writeInstruction(InstructionBuilder.getId());
        instruction = stringToInst(waitForMessage());
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        user = new User(username,Integer.parseInt(instruction.getParam("message")));

        configureDB();
    }

    public CompletableFuture<Void> signupWithoutEncryptionFuture(String username, String password) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                signupWithoutEncryption(username, password);
            } catch (IOException | ChatProtocolException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    /**
     * Logs in the user without hashing the password.
     * @param username of the user
     * @param password of the user
     * @throws ChatProtocolException when an error occurs with the message communication
     * @throws IOException when an I/O error occurs in the server communication
     */
    public void loginWithoutEncryption(String username, String password) throws ChatProtocolException, IOException, SQLException {
        socketConnection.writeInstruction(InstructionBuilder.login(username, password));

        Instruction instruction = stringToInst(waitForMessage());

        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        socketConnection.writeInstruction(InstructionBuilder.getId());
        instruction = stringToInst(waitForMessage());
        if (instruction.getName().equals("ERROR")) {
            throw new ChatProtocolException(instruction.getParam("message"));
        }

        user = new User(username,Integer.parseInt(instruction.getParam("message")));

        configureDB();
    }

    public CompletableFuture<Void> loginWithoutEncryptionFuture(String username, String password) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                loginWithoutEncryption(username, password);
            } catch (IOException | ChatProtocolException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
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

    public CompletableFuture<Boolean> invokeDoneFuture() {
        CompletableFuture<Boolean> f = CompletableFuture.supplyAsync(() -> {
            try {
                return invokeDone();
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        },threadPool);
        return f;
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

    public CompletableFuture<String> invokeOutputFuture(String message) {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                return invokeOutput(message);
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
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

    public CompletableFuture<Boolean> isOnlineFuture(String username) {
        CompletableFuture<Boolean> f = CompletableFuture.supplyAsync(() -> {
            try {
                return isOnline(username);
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
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

        MessageDB messageDB = new MessageDB(this);
        messageDB.connect(user.getUsername());

        if (!messageDB.containsChat(username)) {
            messageDB.createChat(username);
        }

        Message msg = new Message(user.getUsername(), message);

        messageDB.addMessage(msg, username);

        messageDB.close();
    }

    public CompletableFuture<Void> sendMessageFuture(String message, String username) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                sendMessage(message, username);
            } catch (IOException | ChatProtocolException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public void sendFile(String filePath, String username) throws IOException, ChatProtocolException, SQLException {
        Path path = Path.of(filePath);
        long fileSize = Files.size(path);
        if(fileSize > 2 * 1024 * 1024) {
            throw new IOException("sending files supports only 2MB max");
        }
        String fileName = path.getFileName().toString();
        byte[] bytes = Files.readAllBytes(path);
        String base64 = Base64.getEncoder().encodeToString(bytes);

        Instruction instruction = InstructionBuilder.sendFile(username, fileName, base64);
        socketConnection.writeInstruction(instruction);

        Instruction response = stringToInst(waitForMessage());

        if(response.getName().equals("ERROR")){
            throw new ChatProtocolException(response.getParam("message"));
        }

        MessageDB messageDB = new MessageDB(this);
        messageDB.connect(user.getUsername());

        if (!messageDB.containsChat(username)) {
            messageDB.createChat(username);
        }

        File file = new File(user.getUsername(), fileName);

        messageDB.addFile(user, file, base64, username);

        messageDB.close();

    }

    public CompletableFuture<Void> sendFileFuture(String filePath, String username) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                sendFile(filePath, username);
            } catch (IOException | ChatProtocolException | SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public void autoSave(boolean value) throws IOException, ChatProtocolException {
        Instruction instruction = InstructionBuilder.autoMessageSave(value);

        socketConnection.writeInstruction(instruction);

        Instruction response = stringToInst(waitForMessage());

        if(response.getName().equals("ERROR")){
            throw new ChatProtocolException(response.getParam("message"));
        }
    }

    public CompletableFuture<Void> autoSaveFuture(boolean value) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                autoSave(value);
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public void saveToDatabase(String username,String message) throws IOException, ChatProtocolException {
        Instruction instruction = InstructionBuilder.saveToDatabase(username,message);

        socketConnection.writeInstruction(instruction);

        Instruction response = stringToInst(waitForMessage());

        if(response.getName().equals("ERROR")){
            throw new ChatProtocolException(response.getParam("message"));
        }
    }

    public CompletableFuture<Void> saveToDatabaseFuture(String username, String message) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                saveToDatabase(username, message);
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public void getFromDatabase() throws IOException, ChatProtocolException {
        Instruction instruction = InstructionBuilder.getFromDatabase();

        Instruction[] msgs = readArray(instruction);

        for(Instruction i : msgs){
            String username = i.getParam("sender");
            String message = i.getParam("message");

            try{
                MessageDB messageDB = getDatabase();

                if (!messageDB.containsChat(username)) {
                    messageDB.createChat(username);
                }

                messageDB.addMessage(new Message(username, message), user.getUsername());

            } catch (SQLException e){
                throw new ChatProtocolException("Failed to save message");
            } catch (ClientNotLoggedInException e){
                throw new ChatProtocolException("User is not logged in");
            }
        }

    }

    public CompletableFuture<Void> getFromDatabaseFuture() {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                getFromDatabase();
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public boolean userExists(String username) throws IOException, ChatProtocolException {
        Instruction instruction = InstructionBuilder.exists(username);

        socketConnection.writeInstruction(instruction);

        Instruction response = stringToInst(waitForMessage());

        if(response.getName().equals("ERROR")){
            throw new ChatProtocolException(response.getParam("messasge"));
        } else if(response.getName().equals("TRUE")){
            return true;
        }

        return false;
    }

    public CompletableFuture<Boolean> userExistsFuture(String username) {
        CompletableFuture<Boolean> f = CompletableFuture.supplyAsync(() -> {
            try {
                return userExists(username);
            } catch (IOException | ChatProtocolException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    public void addChat(String chat) throws SQLException {
        MessageDB messageDB = new MessageDB(this);

        messageDB.connect(user.getUsername());
        if(!messageDB.containsChat(chat)){
            messageDB.createChat(chat);
        }

        messageDB.close();
    }

    public CompletableFuture<Void> addChatFuture(String username) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                addChat(username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, threadPool);
        return f;
    }

    //endregion
    /**
     * Waits until the next message from the server is sent.
     * @return the message that the server sent
     */
    // What the fuck!!!
    // I hate my past self.
    public String waitForMessage() {
        message = null;
        synchronized (messageLock) {
            while(message == null) {
                try {
                    messageLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return message;
    }

    private Instruction[] readArray(Instruction instruction) throws IOException, ChatProtocolException {
        ArrayList<Instruction> instructionList = new ArrayList<>();

        socketConnection.writeInstruction(instruction);

        Instruction response = stringToInst(waitForMessage());

        if(response.getName().equals("ARRAY")){
            int count = Integer.parseInt(response.getParam("count"));

            for(int i = 0; i < count;i++){
                socketConnection.writeInstruction(InstructionBuilder.next());
                Instruction inst = stringToInst(waitForMessage());

                if(inst.getName().equals("END")){
                    return instructionList.toArray(Instruction[]::new);
                } else if(inst.getName().equals("ERROR")){
                    throw new ChatProtocolException(instruction.getParam("message"));
                }

                instructionList.add(inst);
            }

            socketConnection.writeInstruction(InstructionBuilder.done());
        } else {
            System.out.println(response);
            throw new ChatProtocolException("Unexpected response");
        }
        return new Instruction[0];
    }

    /**
     * This method is used to get all chats from the database
     * @return all chat names
     * @throws SQLException when an SQL error occurs
     */
    public String[] getChats() throws SQLException, ClientNotLoggedInException {
        if(user == null){
            throw new ClientNotLoggedInException("Client is not logged in");
        }

        MessageDB messageDB = new MessageDB(this);
        messageDB.connect(user.getUsername());

        String[] chats = messageDB.getChats();

        messageDB.close();

        return chats;
    }

    /**
     * This method is used to get messages from a chat.
     * @param chat with the messages
     * @param count number of the messages that should be returned
     * @return the messages
     * @throws SQLException when SQL error occurs
     */
    public ChatMedia[] getMessages(String chat, int count) throws SQLException, ClientNotLoggedInException {
        if(user == null){
            throw new ClientNotLoggedInException("Client is not logged in");
        }

        MessageDB messageDB = new MessageDB(this);

        messageDB.connect(user.getUsername());

        ChatMedia[] messages = messageDB.getMessages(chat,count);

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
            // 25.11 2025: what is that, this is very bad
            // TODO: do something with this stupid code
            // make it not send a fucking request to the server
            // after a local String to Instruction conversion this is stupid
        }
    }

    @Override
    public void messageRead(String msg) {
        message = msg;
        synchronized(messageLock) {
            messageLock.notify();
        }
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }

    public MessageDB getDatabase() throws ClientNotLoggedInException, SQLException {
        if(user == null){
            throw new ClientNotLoggedInException("Client is not logged in");
        }

        return configureDBNoClose();
    }

    public User getUser() {
        return user;
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

    public ClientConnectionHandler getClientConnectionHandler() {
        return clientConnectionHandler;
    }
    public void setFileDir(String fileDir){
        fileCreator.setFileDir(fileDir);
    }
}
