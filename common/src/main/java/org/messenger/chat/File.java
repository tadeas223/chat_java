package org.messenger.chat;

import java.util.Date;

public class File {
    String contents;
    String username;
    String fileName;
    Date date;

    public String getUsername() {
        return username;
    }

    public String getContents() {
        return contents;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getDate() {
        return date;
    }

    public File(String username, String fileName, String contents, Date date) {
        this.username = username;
        this.fileName = fileName;
        this.contents = contents;
        this.date = date;
    }

    public File(String username, String fileName, String contents) {
        this.username = username;
        this.fileName = fileName;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "FILE{" +
                "username='" + username + '\'' +
                ", contents='" + contents + '\'' +
                ", fileName='" + fileName + '\'' +
                ", date=" + date +
                '}';
    }
}
