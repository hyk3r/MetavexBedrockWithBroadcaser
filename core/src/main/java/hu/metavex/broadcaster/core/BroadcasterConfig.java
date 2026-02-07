package hu.metavex.broadcaster.core;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class BroadcasterConfig {
    private String sessionName = "Minecraft Server";
    private String serverIp = "127.0.0.1";
    private int serverPort = 19132;
    private boolean autoUpdateServer = true;
    private boolean autoRestartServer = true;

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isAutoUpdateServer() {
        return autoUpdateServer;
    }

    public void setAutoUpdateServer(boolean autoUpdateServer) {
        this.autoUpdateServer = autoUpdateServer;
    }

    public boolean isAutoRestartServer() {
        return autoRestartServer;
    }

    public void setAutoRestartServer(boolean autoRestartServer) {
        this.autoRestartServer = autoRestartServer;
    }
}
