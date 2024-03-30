package org.server;

import org.security.User;

import java.sql.*;

public class SQLConnection {
    private final String db = "secret";
    private final String user = "secret";
    private final String password = "secret";

    private final String ip = "secret";
    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://"+ip+":3306/"+db,user,password);
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
}
