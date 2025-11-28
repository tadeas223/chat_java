package org.messenger.chat;

import java.util.Date;

/**
 * This class is a code representation of a message.
 * It only holds a username, message, date variables.
 */
public class Message implements ChatMedia {
    String username;
    String message;
    Date date;

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public Message(String username, String message, Date date) {
        this.username = username;
        this.message = message;
        this.date = date;
    }

    public Message(String username, String message) {
        this.username = username;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
