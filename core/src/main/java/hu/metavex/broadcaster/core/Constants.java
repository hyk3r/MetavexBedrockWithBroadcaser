package hu.metavex.broadcaster.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v776.Bedrock_v776; // Using a known stable version or check newer
import org.cloudburstmc.protocol.bedrock.codec.v898.Bedrock_v898; 

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Constants {
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantConverter())
        .registerTypeAdapter(Date.class, new DateConverter())
        .disableHtmlEscaping()
        .create();

    public static final Gson GSON_NULLS = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantConverter())
        .registerTypeAdapter(Date.class, new DateConverter())
        .serializeNulls()
        .create();

    public static final String SERVICE_CONFIG_ID = "4fc10100-5f7a-4470-899b-280835760c07";
    public static final String TEMPLATE_NAME = "MinecraftLobby";
    public static final String TITLE_ID = "896928775";
    public static final String CREATE_SESSION = "https://sessiondirectory.xboxlive.com/serviceconfigs/" + SERVICE_CONFIG_ID + "/sessionTemplates/" + TEMPLATE_NAME + "/sessions/%s";
    
    // Fixed URI formatting for handle url
    public static final String JOIN_SESSION = "https://sessiondirectory.xboxlive.com/handles/%s/session";

    public static final URI RTA_WEBSOCKET = URI.create("wss://rta.xboxlive.com/connect");
    public static final URI CREATE_HANDLE = URI.create("https://sessiondirectory.xboxlive.com/handles");
    public static final String JOIN_HANDLE = "https://sessiondirectory.xboxlive.com/handles";

    public static final String PEOPLE = "https://social.xboxlive.com/users/me/people/xuid(%s)";
    public static final String USER_PRESENCE = "https://userpresence.xboxlive.com/users/xuid(%s)/devices/current/titles/current";
    public static final URI SOCIAL_SUMMARY = URI.create("https://social.xboxlive.com/users/me/summary");

    public static final Duration WEBSOCKET_CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    public static final int MAX_FRIENDS = 2000;
    
    // Using latest supported codec from the library we are using
    // Note: The original code imported v898, depending on the library version we have.
    // I will use v898 if available, or fallback to what is available in the libs.
    // Since we are using protocol-codec "3.0.0.Beta11-20251210.195537-15", it should have recent codecs.
    public static final BedrockCodec BEDROCK_CODEC = Bedrock_v898.CODEC; 
}
