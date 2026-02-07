package hu.metavex.broadcaster.core;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RtaWebsocketClient extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RtaWebsocketClient.class);
    private final SessionManager sessionManager;
    private final String xuid;
    
    private String connectionId;
    private boolean isFirstConnection = true;
    private final CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();

    public RtaWebsocketClient(SessionManager sessionManager, String xuid, String authHeader) {
        super(Constants.RTA_WEBSOCKET);
        addHeader("Authorization", authHeader);
        this.sessionManager = sessionManager;
        this.xuid = xuid;
    }

    public CompletableFuture<String> getConnectionIdFuture() {
        return connectionIdFuture;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send("[1,1,\"https://sessiondirectory.xboxlive.com/connections/\"]");
    }

    @Override
    public void onMessage(String message) {
        try {
            Object[] parts = Constants.GSON.fromJson(message, Object[].class);
            if (parts == null || parts.length == 0) return;
            
            MessageType type = MessageType.fromValue(((Double) parts[0]).intValue());
            if (type == null) return;

            switch (type) {
                case Subscribe:
                    LOGGER.debug("RTA Subscribe: {}", message);
                    if (message.contains("ConnectionId") && isFirstConnection) {
                         // GSON parses generic JSON objects as LinkedTreeMap
                        @SuppressWarnings("unchecked")
                        Map<String, String> data = (Map<String, String>) parts[4];
                        connectionId = data.get("ConnectionId");
                        connectionIdFuture.complete(connectionId);
                        isFirstConnection = false;
                        
                        // Subscribe to friends? Maybe later if we want friend sync.
                    }
                    break;
                case Unsubscribe:
                    LOGGER.debug("RTA Unsubscribe: {}", message);
                    break;
                case Event:
                    LOGGER.debug("RTA Event: {}", message);
                    break;
                case Resync:
                    LOGGER.debug("RTA Resync: {}", message);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error handling RTA message: " + message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (!connectionIdFuture.isDone()) {
            connectionIdFuture.completeExceptionally(new Exception("RTA Websocket disconnected before connectionId was received"));
        }
        LOGGER.info("RTA Websocket disconnected: {} ({})", reason, code);
        // Let SessionManager know so it can reconnect?
        // SessionManager should check connection health periodically.
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.error("RTA Websocket error", ex);
    }
}
