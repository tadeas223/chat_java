package org.server.socketData;

import org.security.User;

public class AuthenticationData implements SocketData{
    private User user;

    public AuthenticationData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
