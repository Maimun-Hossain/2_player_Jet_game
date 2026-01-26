package com.example.jetgame.model.websocket;

/**
 * Represents an action initiated by a player, sent from the client to the server via WebSocket.
 * Contains the player's identifier and the type of action performed (e.g., "UP", "DOWN", "SHOOT").
 */
public class PlayerAction {
    private String player; // The name of the player performing the action
    private String action; // The type of action (e.g., "UP", "DOWN", "SHOOT")

    /**
     * Default constructor for PlayerAction.
     */
    public PlayerAction() {
    }

    /**
     * Constructs a new PlayerAction instance.
     * @param player The name of the player.
     * @param action The action performed.
     */
    public PlayerAction(String player, String action) {
        this.player = player;
        this.action = action;
    }

    // --- Getters and Setters ---

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
