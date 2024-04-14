package org.client;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MessageDB {
    private String databaseFile = "data/messages.db";
    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:"+databaseFile);
    }

    public void createChatsTable() throws SQLException {
        String query = "CREATE TABLE chats (" +
                "  `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  `name` VARCHAR(45));";

        Statement statement = connection.createStatement();
        statement.execute(query);
    }

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

    public void addMessage(Message message,String chat) throws SQLException {
        String query = "INSERT INTO "+chat+" (username,message,date) " +
                "VALUES (?,?,DATETIME('now'));";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,message.getUsername());
        statement.setString(2,message.getMessage());

        statement.execute();
    }

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


    public void close() throws SQLException {
        connection.close();
    }
}
