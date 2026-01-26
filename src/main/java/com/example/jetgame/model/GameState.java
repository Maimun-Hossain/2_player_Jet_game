package com.example.jetgame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the entire current state of the game, including all active players,
 * bullets, and power-ups. This object is regularly sent to clients via WebSocket
 * to synchronize the game.
 */
public class GameState {

    private List<Player> players = new ArrayList<>(); // List of active players in the game
    private List<Bullet> bullets = new ArrayList<>(); // List of active bullets currently in flight
    private List<PowerUp> powerUps = new ArrayList<>(); // List of active power-ups on the game map

    /**
     * Default constructor for GameState. Initializes empty lists for game entities.
     */
    public GameState() {
    }

    // --- Getters and Setters ---

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public void setPowerUps(List<PowerUp> powerUps) {
        this.powerUps = powerUps;
    }
}
