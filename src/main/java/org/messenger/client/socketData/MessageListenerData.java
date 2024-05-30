package org.messenger.client.socketData;

import org.messenger.connection.socketData.SocketData;

import java.util.ArrayList;

public class MessageListenerData implements SocketData {
    ArrayList<MessageListener> listeners = new ArrayList<>();

    public ArrayList<MessageListener> getListeners() {
        return listeners;
    }

    public void setListeners(ArrayList<MessageListener> listeners) {
        this.listeners = listeners;
    }

    public void addListener(MessageListener listener){
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener){
        listeners.remove(listener);
    }

    public void callListeners(){
        for (MessageListener l : listeners){
            l.messageReceived();
        }
    }
}
