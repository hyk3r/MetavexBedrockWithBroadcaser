package hu.metavex.broadcaster.core.models.session;

import hu.metavex.broadcaster.core.Constants;
import hu.metavex.broadcaster.core.SessionInfo;

import java.util.HashMap;
import java.util.Map;

public record CreateSessionRequest(
    Map<String, Object> properties,
    Map<String, Object> members,
    int version
) {
    public static CreateSessionRequest create(SessionInfo sessionInfo, String xuid) {
        Map<String, Object> systemProperties = new HashMap<>();
        systemProperties.put("hostName", sessionInfo.getHostName());
        systemProperties.put("worldName", sessionInfo.getWorldName());
        systemProperties.put("version", sessionInfo.getVersion());
        systemProperties.put("protocol", sessionInfo.getProtocol());
        systemProperties.put("memberCount", sessionInfo.getPlayers());
        systemProperties.put("maxMemberCount", sessionInfo.getMaxPlayers());
        systemProperties.put("joinRestriction", "public"); // or "followed" based on logic
        systemProperties.put("broadcastSetting", 3);
        systemProperties.put("onlineCrossPlatformGame", true);
        systemProperties.put("crossPlayDisabled", false);
        systemProperties.put("titleId", 0); // Need to check if 0 is valid or if we need a real title ID. Typically 0 works for custom servers or specific ID. 
        // Original code used Constants.TITLE_ID? Or 896928775 (Minecraft Bedrock Title ID)
        systemProperties.put("transportLayer", 0); // 0 = Raquel ?
        // NetherNet requires connection info. 
        
        // Let's check original SessionManagerCore logic for properties construction.
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("system", systemProperties);
        properties.put("custom", new HashMap<>());

        Map<String, Object> me = new HashMap<>();
        Map<String, Object> meConstants = new HashMap<>();
        Map<String, Object> meSystem = new HashMap<>();
        meSystem.put("xuid", xuid);
        meSystem.put("initialize", true);
        meConstants.put("system", meSystem);
        me.put("me", meConstants);

        return new CreateSessionRequest(properties, me, 1);
    }
}
