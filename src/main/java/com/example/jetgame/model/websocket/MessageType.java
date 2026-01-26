package com.example.jetgame.model.websocket;

/**
 * Defines the different types of WebSocket messages that can be exchanged
 * between the server and game clients.
 */
public enum MessageType {
    JOIN,                // A player is joining the game
    MOVE,                // A player is moving their jet
    SHOOT,               // A player is firing a bullet
    GAME_START,          // The game is starting
    GAME_OVER,           // The game has ended
    SCORE_UPDATE,        // General game state update, including scores, positions, etc.
    POWERUP_SPAWN,       // A new power-up has appeared (might not be directly used as full state is sent)
    POWERUP_COLLECT,     // A power-up has been collected (might not be directly used as full state is sent)
    WAITING_FOR_PLAYER   // A player is waiting for another player to join
}
