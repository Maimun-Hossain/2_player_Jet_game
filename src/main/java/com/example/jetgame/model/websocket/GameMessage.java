package com.example.jetgame.model.websocket;

/**
 * A generic wrapper for WebSocket messages sent between the server and clients.
 * It encapsulates a message type and a flexible payload object.
 */
public class GameMessage {
    private MessageType type; // The type of the message, indicating its purpose
    private Object payload;   // The actual data being sent, can be any object

    /**
     * Constructs a new GameMessage.
     * @param type The MessageType of this message.
     * @param payload The data payload associated with this message.
     */
    public GameMessage(MessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    // --- Getters and Setters ---

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
