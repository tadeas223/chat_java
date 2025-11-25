package org.messenger.client;

import org.messenger.chat.File;
import org.messenger.chat.Message;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is used to communicate with a local SQLite database.
 * The database is used to save chats and messages.
 */
public class MessageDB {
    private final String DB_FILE = "client/";
    private final Client client;
    private Connection connection;

    public MessageDB(Client client) {
        this.client = client;
    }

    /**
     * This method needs to be called before any other method is called.
     * If it is not called, this class will not be connected to the
     * local database ane every other method will throw an exception.
     * @throws SQLException When the method fails to connect to the database.
     */
    public void connect(String dbName) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + client.getFileCreator().getAbsolutePath(dbName+".db"));
    }

    private String getDatabasePath() throws IOException {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(DB_FILE)).getPath();
    }

    /**
     * Initializes a chat table.
     * This table needs to exist in the database to look up all chats in it.
     * This method should be called at the creation of the database.
     * @throws SQLException when an error occurs when creating the table
     */
    public void createChatsTable() throws SQLException {
        String query = "CREATE TABLE chats (" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  `name` VARCHAR(45));";

        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    /**
     * Creates a new chat with the inputted name, and adds the chat into chats table.
     * @param name of the chat
     * @throws SQLException when an error occurs when adding the chat
     */
    public void createChat(String name) throws SQLException {
        String query = "CREATE TABLE "+name+" (" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  `username` VARCHAR(45)," +
                "  `message` VARCHAR(255)," +
                "  `date` DATETIME);";

        PreparedStatement statement = connection.prepareStatement(query);

        statement.execute();

        query = "INSERT INTO chats (name) VALUES (?)";

        statement = connection.prepareStatement(query);
        statement.setString(1,name);
        statement.execute();
    }

    /**
     * Adds a message to a chat.
     * @param message to be added to a chat
     * @param chat where the message should be added
     * @throws SQLException when an error occurs when adding the message
     */
    public void addMessage(Message message,String chat) throws SQLException {
        String query = "INSERT INTO "+chat+" (username,message,date) " +
                "VALUES (?,?,DATETIME('now'));";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,message.getUsername());
        statement.setString(2,message.getMessage());

        statement.execute();
    }

    /**
     * This method returns the last messages from a chat.
     * @param chatName name of the chat with the messages
     * @param count number of messages that should be returned
     * @return the messages from the chat
     * @throws SQLException when an error occurs when getting the messages
     */
    public Message[] getMessages(String chatName,int count) throws SQLException {
        ArrayList<Message> messageList = new ArrayList<>();

        String query = "SELECT * FROM "+chatName+" ORDER BY id DESC LIMIT " + count;
        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();

        for(int i = 0; i < count; i++){
            if(!resultSet.next()) break;

            String username = resultSet.getString("username");
            String message = resultSet.getString("message");
            Date date = resultSet.getDate("date");

            messageList.add(new Message(username,message,date));
        }

        return messageList.toArray(Message[]::new);
    }

    /**
     * This method is used to find if the chat exists or not.
     * @param chat that needs to be checked
     * @return true of the chat exists, false if not
     * @throws SQLException when an error occurs while checking the chat
     */
    public boolean containsChat(String chat) throws SQLException {
        String query = "SELECT EXISTS " +
                "(SELECT name FROM sqlite_schema" +
                " WHERE type='table' AND name=?);";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,chat);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        if(resultSet.getInt(1) == 1){
            return true;
        }
        return false;
    }

    /**
     * This chat is used to find if the chats table exists or not.
     * @return true if the chats table exists, false if not
     * @throws SQLException when an error occurs while checking the chats table.
     */
    public boolean containsChatsTable() throws SQLException {
        String query = "SELECT EXISTS " +
                "(SELECT name FROM sqlite_schema" +
                " WHERE type='table' AND name='chats');";

        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();

        if(resultSet.getInt(1) == 1){
            return true;
        }
        return false;
    }

    public String[] getChats() throws SQLException {
        String query = "SELECT name FROM chats";
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<String> chats = new ArrayList<>();

        while(resultSet.next()){
            chats.add(resultSet.getString("name"));
        }

        return chats.toArray(String[]::new);
    }


    /**
     * Closes the database connection.
     * This method should be called at the end of the use of this class.
     * @throws SQLException when an error occurs while closing the database connection
     */
    public void close() throws SQLException {
        connection.close();
    }

    public void addFile(File file, String username) {
        System.out.println("MessageDB.addFile -> not implemented :(");
    }
}
