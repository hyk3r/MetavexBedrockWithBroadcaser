package hu.metavex.broadcaster.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionInfo {
    private static final Pattern COLOR_PATTERN = Pattern.compile("\u00A7[\\w]");

    private String hostName;
    private String worldName;
    private int players;
    private int maxPlayers;
    private String ip;
    private int port;
    
    // Additional fields for session management
    private String sessionId;
    private String connectionId;
    private String handleId;
    private String xuid;
    private Long netherNetId;

    public SessionInfo() {
    }

    public SessionInfo(String hostName, String worldName, int players, int maxPlayers, String ip, int port) {
        this.hostName = hostName;
        this.worldName = worldName;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.ip = ip;
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = removeColorCodes(hostName);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = removeColorCodes(worldName);
    }
    
    public String getVersion() {
        return Constants.BEDROCK_CODEC.getMinecraftVersion();
    }

    public int getProtocol() {
        return Constants.BEDROCK_CODEC.getProtocolVersion();
    }

    public int getPlayers() {
        if (players <= 0) return 1;
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getMaxPlayers() {
        if (maxPlayers <= getPlayers()) {
            return getPlayers() + 1;
        }
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getHandleId() {
        return handleId;
    }

    public void setHandleId(String handleId) {
        this.handleId = handleId;
    }
    
    public String getXuid() {
        return xuid;
    }

    public void setXuid(String xuid) {
        this.xuid = xuid;
    }
    
    public Long getNetherNetId() {
        return netherNetId;
    }

    public void setNetherNetId(Long netherNetId) {
        this.netherNetId = netherNetId;
    }

    private static String removeColorCodes(String string) {
        if (string == null) return "";
        Matcher matcher = COLOR_PATTERN.matcher(string);
        return matcher.replaceAll("");
    }
}
