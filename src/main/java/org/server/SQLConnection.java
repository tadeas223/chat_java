package org.server;

import org.chat.Message;
import org.security.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is user for communication with an sql database.
 * This class has methods for easier data saving and retrieving from the database.
 */
public class SQLConnection {
    private final String db = "messenger";
    private final String user = "tad";
    private final String password = "tadeas223";

    private final String ip = "192.168.0.150";
    private Connection connection;

    /**
     * Connects to the database.
     *
     * @throws SQLException when the connection fails
     */
    public void connect() throws SQLException {
        //connection = DriverManager.getConnection("jdbc:mysql://"+ip+":3306/"+db,user,password);
    }

    /**
     * This method closes the connection, and it should be called at the end of use of this class.
     *
     * @throws SQLException when the close operation fails
     */
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * This method is used for retrieving a user from the database based on the username and a password.
     *
     * @param username of the user
     * @param password of the user
     * @return {@link User} or null if the user does not exist.
     * @throws SQLException when an SQL error occurs
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username=? AND password=?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");

            return new User(username, id);
        } else {
            return null;
        }
    }

    /**
     * This method adds a user to the database and than calls the login() method.
     *
     * @param username of the new user
     * @param password of the new user
     * @return {@link User} that was sighed up
     * @throws SQLException when a SQL error occurs
     */
    public User signup(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        preparedStatement.executeUpdate();

        return login(username, password);
    }

    /**
     * Saves a message to the database.
     *
     * @param message  that should be saved
     * @param sender   of the message
     * @param receiver of the message
     * @throws SQLException when an SQL error occurs
     */
    public void saveMessage(String message, String sender, String receiver) throws SQLException {
        String sql = "INSERT INTO messages (message, sender, receiver, date) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, message);
        preparedStatement.setString(2, sender);
        preparedStatement.setString(3, receiver);

        long currentTimeInMillis = System.currentTimeMillis();

        Date currentDate = new Date(currentTimeInMillis);

        String currentDateTime = currentDate.toString();

        preparedStatement.setString(4, currentDateTime);

        preparedStatement.executeUpdate();
    }

    /**
     * Retrieves all messages that have the inputted receiver from the database.
     * The messages are then deleted, so they cannot be accessed again.
     *
     * @param receiver of the message
     * @return all retrieved messages
     * @throws SQLException when an SQL error occurs
     */
    public Message[] getMessages(String receiver) throws SQLException {
        String sql = "SELECT message,sender,date FROM messages WHERE receiver=?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, receiver);

        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Message> messages = new ArrayList<>();

        while (resultSet.next()) {
            String message = resultSet.getString("message");
            String sender = resultSet.getString("sender");
            Date date = resultSet.getDate("date");

            messages.add(new Message(sender, message, date));
            System.out.println(message);
        }

        sql = "DELETE FROM `messenger`.`messages` WHERE (receiver = ?);";

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, receiver);
        preparedStatement.execute();

        return messages.toArray(Message[]::new);
    }
}
