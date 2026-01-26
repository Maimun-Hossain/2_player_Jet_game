package com.example.jetgame.model;

/**
 * Represents a power-up object that can appear in the game.
 * Players can collect these to gain temporary enhancements.
 */
public class PowerUp {

    /**
     * Enum defining the different types of power-ups available in the game.
     */
    public enum PowerUpType {
        BIG_BULLET,      // Increases bullet size
        FAST_MOVEMENT,   // Increases player movement speed
        DOUBLE_SCORE     // Doubles score gained from hitting opponent
    }

    private PowerUpType type; // The type of this power-up
    private double x;        // X-coordinate of the power-up's top-left corner
    private double y;        // Y-coordinate of the power-up's top-left corner
    private long duration;   // Duration of the power-up effect in milliseconds

    /**
     * Constructs a new PowerUp instance.
     * @param type The type of the power-up.
     * @param x Initial X-coordinate.
     * @param y Initial Y-coordinate.
     * @param duration The duration of the power-up's effect in milliseconds.
     */
    public PowerUp(PowerUpType type, double x, double y, long duration) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.duration = duration;
    }

    // --- Getters and Setters ---

    public PowerUpType getType() {
        return type;
    }

    public void setType(PowerUpType type) {
        this.type = type;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
