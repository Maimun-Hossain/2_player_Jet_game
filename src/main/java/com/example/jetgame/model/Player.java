package com.example.jetgame.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a player (jet) in the game.
 * Tracks its state including position, score, and active power-ups.
 */
public class Player {

    private String name; // The player's unique name
    private double x; // X-coordinate of the jet's top-left corner
    private double y; // Y-coordinate of the jet's top-left corner
    private int width = 50; // Width of the jet (default)
    private int height = 30; // Height of the jet (default)
    private int score; // Current score of the player
    // Map to store active power-ups: PowerUpType -> Expiration Timestamp (System.currentTimeMillis())
    private Map<PowerUp.PowerUpType, Long> activePowerUps = new HashMap<>();

    /**
     * Constructs a new Player instance.
     * @param name The name of the player.
     * @param x Initial X-coordinate.
     * @param y Initial Y-coordinate.
     */
    public Player(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.score = 0; // Players start with a score of 0
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Applies a collected power-up to the player.
     * Stores the power-up type and its expiration time.
     * @param powerUp The PowerUp object that was collected.
     */
    public void applyPowerUp(PowerUp powerUp) {
        activePowerUps.put(powerUp.getType(), System.currentTimeMillis() + powerUp.getDuration());
    }

    /**
     * Checks if the player currently has a specific power-up active.
     * @param type The type of PowerUp to check.
     * @return true if the power-up is active, false otherwise.
     */
    public boolean hasPowerUp(PowerUp.PowerUpType type) {
        // Checks if the power-up exists in the map and if its expiration time is still in the future.
        return activePowerUps.containsKey(type) && activePowerUps.get(type) > System.currentTimeMillis();
    }

    /**
     * Clears all active power-ups from the player.
     * Used typically when a game ends or resets.
     */
    public void clearPowerUps() {
        activePowerUps.clear();
    }

    /**
     * Returns the map of active power-ups for inspection or cleanup.
     * @return A map of active PowerUpTypes to their expiration timestamps.
     */
    public Map<PowerUp.PowerUpType, Long> getActivePowerUps() {
        return activePowerUps;
    }
}
