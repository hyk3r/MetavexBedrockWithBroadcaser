package hu.metavex.broadcaster.core.models.session;

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
        systemProperties.put("titleId", 896928775); // Minecraft Bedrock Title ID
        // Original code used Constants.TITLE_ID? Or 896928775 (Minecraft Bedrock Title ID)
        systemProperties.put("transportLayer", 0); // 0 = Raquel ?
        // NetherNet requires connection info. 
        
        // Let's check original SessionManagerCore logic for properties construction.
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("system", systemProperties);
        properties.put("custom", new HashMap<>());

        Map<String, Object> me = new HashMap<>();

        Map<String, Object> constants = new HashMap<>();
        Map<String, Object> constantsSystem = new HashMap<>();
        constantsSystem.put("xuid", xuid);
        constants.put("system", constantsSystem);

        Map<String, Object> memberProperties = new HashMap<>();
        Map<String, Object> memberSystemProperties = new HashMap<>();
        memberSystemProperties.put("initialize", true);
        memberProperties.put("system", memberSystemProperties);

        Map<String, Object> memberPayload = new HashMap<>();
        memberPayload.put("constants", constants);
        memberPayload.put("properties", memberProperties);
        me.put("me", memberPayload);

        return new CreateSessionRequest(properties, me, 1);
    }
}
