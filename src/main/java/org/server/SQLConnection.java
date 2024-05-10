package org.server;

import org.chat.Message;
import org.security.User;

import java.sql.*;
import java.util.ArrayList;

public class SQLConnection {
    private final String db = "messenger";
    private final String user = "tad";
    private final String password = "tadeas223";

    private final String ip = "192.168.0.150";
    private Connection connection;

    public void connect() throws SQLException {
        //connection = DriverManager.getConnection("jdbc:mysql://"+ip+":3306/"+db,user,password);
    }

    public void close() throws SQLException {
        connection.close();
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username=? AND password=?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1,username);
        preparedStatement.setString(2,password);

        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            int id = resultSet.getInt("id");

            return new User(username,id);
        } else {
            return null;
        }
    }

    public User signup(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1,username);
        preparedStatement.setString(2,password);

        preparedStatement.executeUpdate();

        return login(username, password);
    }

    public void saveMessage(String message, String sender, String receiver) throws SQLException {
        String sql = "INSERT INTO messages (message, sender, receiver, date) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1,message);
        preparedStatement.setString(2,sender);
        preparedStatement.setString(3,receiver);

        long currentTimeInMillis = System.currentTimeMillis();

        Date currentDate = new Date(currentTimeInMillis);

        String currentDateTime = currentDate.toString();

        preparedStatement.setString(4,currentDateTime);

        preparedStatement.executeUpdate();
    }

    public Message[] getMessages(String receiver) throws SQLException {
        String sql = "SELECT message,sender,date FROM messages WHERE receiver=?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, receiver);

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Message> messages = new ArrayList<>();

        while(resultSet.next()){
            String message = resultSet.getString("message");
            String sender = resultSet.getString("sender");
            Date date = resultSet.getDate("date");

            messages.add(new Message(sender,message,date));
            System.out.println(message);
        }

        sql = "DELETE FROM `messenger`.`messages` WHERE (receiver = ?);";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, receiver);
        preparedStatement.execute();

        return messages.toArray(Message[]::new);
    }
}
