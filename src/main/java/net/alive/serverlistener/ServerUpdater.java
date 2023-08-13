package net.alive.serverlistener;

import net.alive.serverlistener.utils.ApiInteractionUtil;

public class ServerUpdater {

    // The current server status.
    private ServerStatus serverStatus = ServerStatus.UNKNOWN;

    public ServerUpdater() {
        update();
    }

    public void update() {
        updateSettings();
        updateItems();
    }

    public void updateSettings() {

    }

    public void updateItems() {

    }

    /*
        * Checks the connection to the web server and sets the server status accordingly.
     */
    public boolean checkConnection() {
        boolean testConnection = ApiInteractionUtil.checkWebServerConnection(ApiInteractionUtil.API_URL);

        if(testConnection) {
            serverStatus = ServerStatus.ONLINE;
            return true;
        } else {
            serverStatus = ServerStatus.OFFLINE;
            return false;
        }
    }

    /*
        * Returns the current server status.
     */
    public enum ServerStatus {
        ONLINE,
        OFFLINE,
        MAINTENANCE,
        UNKNOWN
    }

}

