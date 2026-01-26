package com.example.jetgame.model;

/**
 * Represents a bullet fired by a player in the game.
 * Tracks its position, speed, dimensions, and the name of the player who fired it.
 */
public class Bullet {

    private double x; // X-coordinate of the bullet's top-left corner
    private double y; // Y-coordinate of the bullet's top-left corner
    private double speed; // Speed and direction of the bullet (positive for right, negative for left)
    private double width; // Width of the bullet
    private double height; // Height of the bullet
    private String shooterName; // Name of the player who fired this bullet

    /**
     * Constructs a new Bullet instance.
     * @param x Initial X-coordinate.
     * @param y Initial Y-coordinate.
     * @param speed Speed and direction of the bullet.
     * @param width Width of the bullet.
     * @param shooterName The name of the player who fired this bullet.
     */
    public Bullet(double x, double y, double speed, double width, String shooterName) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = 5; // Default bullet height, can be overridden if needed
        this.shooterName = shooterName;
    }

    // --- Getters and Setters ---

    public String getShooterName() {
        return shooterName;
    }

    public void setShooterName(String shooterName) {
        this.shooterName = shooterName;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
