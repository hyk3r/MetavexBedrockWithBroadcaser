package hu.metavex.broadcaster.core.nethernet;

import hu.metavex.broadcaster.core.SessionInfo;
import hu.metavex.broadcaster.core.SessionManager;
import hu.metavex.broadcaster.core.nethernet.initializer.NetherNetBedrockChannelInitializer;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;

public class BroadcasterChannelInitializer extends NetherNetBedrockChannelInitializer<BedrockServerSession> {

    private final SessionInfo sessionInfo;
    private final SessionManager sessionManager;

    public BroadcasterChannelInitializer(SessionInfo sessionInfo, SessionManager sessionManager) {
        this.sessionInfo = sessionInfo;
        this.sessionManager = sessionManager;
    }

    @Override
    protected BedrockServerSession createSession0(BedrockPeer peer, int subClientId) {
        return new BedrockServerSession(peer, subClientId);
    }

    @Override
    protected void initSession(BedrockServerSession session) {
        session.setLogging(true);
        session.setPacketHandler(new RedirectPacketHandler(session, sessionInfo, sessionManager));
    }
}
