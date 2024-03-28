package org.connection;

/**
 * This Listener is used with {@link SocketConnection}.
 * This class works as a catcher for incoming messages that were read by the {@link SocketConnection}.
 */
public interface MsgReadListener {
    /**
     * This Method is called every time the message was read
     * @param msg the message that was read
     */
    void messageRead(String msg);
}
