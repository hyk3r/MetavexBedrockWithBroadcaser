package hu.metavex.broadcaster.core.models.session;

public record CreateHandleRequest(
    int version,
    String type,
    SessionRef sessionRef
) {
    public static CreateHandleRequest create(String serviceConfigId, String templateName, String sessionName) {
        return new CreateHandleRequest(1, "activity", new SessionRef(serviceConfigId, templateName, sessionName));
    }

    public record SessionRef(
        String scid,
        String templateName,
        String name
    ) {}
}
