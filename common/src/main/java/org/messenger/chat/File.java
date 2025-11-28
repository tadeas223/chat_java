package org.messenger.chat;

import java.util.Date;

public class File implements ChatMedia {
    String username;
    String fileName;
    Date date;

    public String getUsername() {
        return username;
    }


    public String getFileName() {
        return fileName;
    }

    public Date getDate() {
        return date;
    }

    public File(String username, String fileName, Date date) {
        this.username = username;
        this.fileName = fileName;
        this.date = date;
    }

    public File(String username, String fileName) {
        this.username = username;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FILE{" +
                "username='" + username + '\'' +
                ", fileName='" + fileName + '\'' +
                ", date=" + date +
                '}';
    }
}
