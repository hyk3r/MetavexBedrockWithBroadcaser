package hu.metavex.broadcaster.core;

public enum MessageType {
    Subscribe(1),
    Unsubscribe(2),
    Event(3),
    Resync(4);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public static MessageType fromValue(int value) {
        for (MessageType type : MessageType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
