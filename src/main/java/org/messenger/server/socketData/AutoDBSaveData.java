package org.messenger.server.socketData;

import org.messenger.connection.socketData.SocketData;

public class AutoDBSaveData implements SocketData {
    private boolean autoSave;

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public AutoDBSaveData(boolean autoSave) {
        this.autoSave = autoSave;
    }
}
