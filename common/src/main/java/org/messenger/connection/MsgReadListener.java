package org.messenger.connection;

/**
 * This Listener is used with {@link SocketConnection}.
 * This class works as a catcher for incoming messages that were read by the {@link SocketConnection}.
 */
public interface MsgReadListener {
    /**
     * This Method is called every time the message was read
     *
     *
     * 25.11 2025: this documentation is stupid.
     *
     * @param msg the message that was read
     */
    void messageRead(String msg);
}
