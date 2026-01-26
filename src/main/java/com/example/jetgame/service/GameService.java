package com.example.jetgame.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jetgame.model.Bullet;
import com.example.jetgame.model.GameState;
import com.example.jetgame.model.Leaderboard;
import com.example.jetgame.model.Player;
import com.example.jetgame.model.PowerUp;
import com.example.jetgame.repository.LeaderboardRepository;

/**
 * Core game logic service for the Jet Fighter 2-Player game.
 * Manages the entire game state, player actions, physics updates,
 * collision detection, power-ups, and integrates with the leaderboard.
 * This service operates as a singleton, holding the authoritative game state.
 */
@Service
public class GameService {

    // --- Game World Dimensions and Constants ---
    public static final double GAME_WIDTH = 800; // Width of the game canvas
    public static final double GAME_HEIGHT = 600; // Height of the game canvas
    public static final double PLAYER_HEIGHT = 30; // Height of the player jet
    public static final double PLAYER_WIDTH = 50;  // Width of the player jet (default from Player class)
    public static final double POWERUP_SIZE = 20;  // Side length of a square power-up for collision and rendering
    public static final double BULLET_HEIGHT = 5;  // Default height of a bullet

    // --- Dependencies ---
    private final LeaderboardRepository leaderboardRepository; // Repository for saving game results to the database

    // --- Game State Variables ---
    private GameState gameState = new GameState(); // The current authoritative state of the game, including all entities
    private List<String> playerNames = new ArrayList<>(); // Stores names of connected players waiting to start a game
    private boolean gameOver = false; // Flag indicating if the current game session is over
    private long gameStartTime; // Timestamp (in milliseconds) when the current game session officially started

    // --- Scheduled Tasks ---
    // Manages the spawning of power-ups at regular intervals outside the main game update loop
    private ScheduledExecutorService powerUpSpawner = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random(); // Random number generator for various game events (e.g., power-up type, position)

    /**
     * Constructor for GameService, autowired by Spring.
     * Injects the LeaderboardRepository. The power-up spawner is not started here
     * but upon game start.
     * @param leaderboardRepository Repository for leaderboard data.
     */
    @Autowired
    public GameService(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    /**
     * Starts the power-up spawning mechanism.
     * Ensures any previous spawner is shut down before starting a new one.
     * Power-ups spawn between 5 and 10 seconds.
     */
    public void startPowerUpSpawner() {
        if (powerUpSpawner != null && !powerUpSpawner.isShutdown()) {
            powerUpSpawner.shutdownNow(); // Stop existing spawner
        }
        powerUpSpawner = Executors.newSingleThreadScheduledExecutor();
        powerUpSpawner.scheduleAtFixedRate(this::spawnPowerUp, 5, 10, TimeUnit.SECONDS);
    }

    /**
     * Stops the power-up spawning mechanism.
     * Called when the game ends or is reset.
     */
    public void stopPowerUpSpawner() {
        if (powerUpSpawner != null && !powerUpSpawner.isShutdown()) {
            powerUpSpawner.shutdownNow();
        }
    }

    /**
     * Retrieves the current game state.
     * This is sent to clients to update their game view.
     * @return The current GameState object.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Adds a player to the game.
     * This method handles the logic for a player joining, including resetting the game state
     * if a new session is being started or if the previous game has ended.
     * It initializes player objects and starts the power-up spawner once two players have joined.
     * @param playerName The name of the player attempting to join.
     */
    public void addPlayer(String playerName) {
        // Condition to trigger a full game reset:
        // 1. If 'playerNames' is empty AND 'gameState.getPlayers()' is empty: This indicates the very first player is joining a fresh game.
        // 2. Or, if 'gameOver' is true: A previous game has concluded, and a new session is being initiated.
        if ((playerNames.isEmpty() && gameState.getPlayers().isEmpty()) || gameOver) {
            resetGame(); // Ensure a clean slate for the new game session
        }

        // Add the player's name to a temporary list if not already present.
        // This list tracks who has expressed interest in playing this session.
        if (!playerNames.contains(playerName)) {
            playerNames.add(playerName);
        }

        // Game setup logic:
        // This block executes when two player names have been registered AND the actual Player objects
        // for the 'gameState' have not yet been created (meaning it's a fresh game setup for these two players).
        if (playerNames.size() == 2 && gameState.getPlayers().isEmpty()) {
            // Initialize Player 1 on the left side of the canvas.
            // X-position: 50 pixels from the left edge.
            // Y-position: vertically centered.
            Player player1 = new Player(playerNames.get(0), 50, GAME_HEIGHT / 2 - PLAYER_HEIGHT / 2);
            
            // Initialize Player 2 on the right side of the canvas.
            // X-position: 50 pixels from the right edge, adjusted by Player 1's width to ensure visibility.
            // Y-position: vertically centered.
            Player player2 = new Player(playerNames.get(1), GAME_WIDTH - 50 - player1.getWidth(), GAME_HEIGHT / 2 - PLAYER_HEIGHT / 2);

            // Add the newly created player objects to the game state.
            gameState.getPlayers().add(player1);
            gameState.getPlayers().add(player2);
            
            gameStartTime = System.currentTimeMillis(); // Record the exact start time of the game
            gameOver = false; // Set game over flag to false for the new game
            startPowerUpSpawner(); // Begin spawning power-ups for this game session
        }
    }
    
    /**
     * Resets the entire game state to prepare for a new game session.
     * Clears players, bullets, power-ups, and resets flags and timers.
     */
    public void resetGame() {
        stopPowerUpSpawner(); // Ensure power-ups stop spawning on reset

        // Create a new GameState object to clear all current game entities
        gameState = new GameState();
        playerNames.clear(); // Clear player names to await new players
        gameOver = false;
        gameStartTime = 0; // Reset game start time
    }

    /**
     * Checks if the game is currently in a game over state.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Moves a player's jet vertically based on the given direction.
     * Applies movement speed and enforces game boundaries.
     * @param playerName The name of the player to move.
     * @param direction "UP" or "DOWN".
     */
    public void movePlayer(String playerName, String direction) {
        Player player = getPlayer(playerName);
        if (player != null) {
            double movementSpeed = 10; // Default movement speed
            // Apply FAST_MOVEMENT power-up effect
            if (player.hasPowerUp(PowerUp.PowerUpType.FAST_MOVEMENT)) {
                movementSpeed = 20;
            }

            if ("UP".equals(direction)) {
                // Ensure player doesn't move above the top boundary
                player.setY(Math.max(0, player.getY() - movementSpeed));
            } else if ("DOWN".equals(direction)) {
                // Ensure player doesn't move below the bottom boundary
                player.setY(Math.min(GAME_HEIGHT - PLAYER_HEIGHT, player.getY() + movementSpeed));
            }
        }
    }

    /**
     * Handles a player shooting a bullet.
     * Creates a new bullet with appropriate speed, width, and assigns the shooter.
     * Applies BIG_BULLET power-up effect.
     * @param playerName The name of the player who is shooting.
     */
    public void shoot(String playerName) {
        Player player = getPlayer(playerName);
        if (player != null) {
            double bulletSpeed = 10;
            double bulletWidth = 10;
            // Apply BIG_BULLET power-up effect
            if (player.hasPowerUp(PowerUp.PowerUpType.BIG_BULLET)) {
                bulletWidth = 20;
            }

            // Player 1 (left side) shoots right, Player 2 (right side) shoots left
            if (player.getName().equals(playerNames.get(0))) {
                gameState.getBullets().add(new Bullet(player.getX() + player.getWidth(), player.getY() + player.getHeight() / 2 - BULLET_HEIGHT / 2, bulletSpeed, bulletWidth, playerName));
            } else {
                gameState.getBullets().add(new Bullet(player.getX() - bulletWidth, player.getY() + player.getHeight() / 2 - BULLET_HEIGHT / 2, -bulletSpeed, bulletWidth, playerName));
            }
        }
    }

    /**
     * The main game update loop, called periodically by the GameController.
     * Updates bullet positions, handles collisions, power-up collection, and checks for game over.
     */
    public void update() {
        // Check if game time has expired (60 seconds)
        if (System.currentTimeMillis() - gameStartTime > 60000) {
            gameOver = true;
            stopPowerUpSpawner(); // Stop power-ups from spawning
            saveGameResults(); // Save final scores to the leaderboard
            return; // End game update for this cycle
        }

        List<Bullet> bullets = gameState.getBullets();
        List<Player> players = gameState.getPlayers();
        List<Bullet> bulletsToRemove = new ArrayList<>(); // Bullets that have gone out of bounds or hit a player

        // --- Update Bullet Positions and Check Player Collisions ---
        for (Bullet bullet : bullets) {
            bullet.setX(bullet.getX() + bullet.getSpeed());

            // Remove bullets that move out of the horizontal game bounds
            if (bullet.getX() < 0 || bullet.getX() > GAME_WIDTH) {
                bulletsToRemove.add(bullet);
                continue; // Move to the next bullet
            }

            // Check for collision with players
            for (Player player : players) {
                // A bullet should only hit the opponent, not the player who fired it
                if (bullet.getShooterName().equals(player.getName())) {
                    continue;
                }

                // AABB (Axis-Aligned Bounding Box) collision detection between bullet and player
                if (bullet.getX() < player.getX() + player.getWidth() &&
                    bullet.getX() + bullet.getWidth() > player.getX() &&
                    bullet.getY() < player.getY() + player.getHeight() &&
                    bullet.getY() + bullet.getHeight() > player.getY()) {

                    Player shooter = getPlayer(bullet.getShooterName());
                    if (shooter != null) {
                        int scoreToAdd = 1;
                        // Apply DOUBLE_SCORE power-up effect for the shooter
                        if (shooter.hasPowerUp(PowerUp.PowerUpType.DOUBLE_SCORE)) {
                            scoreToAdd = 2;
                        }
                        shooter.setScore(shooter.getScore() + scoreToAdd); // Update shooter's score
                    }
                    bulletsToRemove.add(bullet); // Remove bullet after hit
                    break; // Bullet hit a player, no need to check other players
                }
            }
        }
        bullets.removeAll(bulletsToRemove); // Efficiently remove all marked bullets

        // --- Process Power-Up Collection by Bullets ---
        List<PowerUp> powerUpsToRemove = new ArrayList<>(); // Power-ups that have been collected
        List<Bullet> bulletsThatHitPowerUp = new ArrayList<>(); // Bullets that collected a power-up

        for (PowerUp powerUp : gameState.getPowerUps()) {
            for (Bullet bullet : bullets) { // Iterate through remaining bullets
                // AABB collision detection between bullet and power-up
                if (bullet.getX() < powerUp.getX() + POWERUP_SIZE &&
                    bullet.getX() + bullet.getWidth() > powerUp.getX() &&
                    bullet.getY() < powerUp.getY() + POWERUP_SIZE &&
                    bullet.getY() + bullet.getHeight() > powerUp.getY()) {

                    Player collectingPlayer = getPlayer(bullet.getShooterName());
                    if (collectingPlayer != null) {
                        collectingPlayer.applyPowerUp(powerUp);
                        // Debug print to confirm power-up collection
                        System.out.println(collectingPlayer.getName() + " collected power-up: " + powerUp.getType() + " with bullet.");
                        powerUpsToRemove.add(powerUp); // Mark power-up for removal
                        bulletsThatHitPowerUp.add(bullet); // Mark bullet for removal
                        break; // Power-up collected, move to next power-up
                    }
                }
            }
        }
        gameState.getPowerUps().removeAll(powerUpsToRemove); // Remove collected power-ups
        bullets.removeAll(bulletsThatHitPowerUp); // Remove bullets that collected power-ups

        // --- Clean Up Expired Player Power-Ups ---
        long currentTime = System.currentTimeMillis();
        for (Player player : players) {
            // Remove power-ups from player's active list if their duration has expired
            player.getActivePowerUps().entrySet().removeIf(entry -> entry.getValue() <= currentTime);
        }
    }

    /**
     * Spawns a new power-up at a random location within the game area.
     * Only spawns if there are two players and the game is not over.
     */
    private void spawnPowerUp() {
        if (gameState.getPlayers().size() < 2 || gameOver) return; // Only spawn in active 2-player game

        PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
        // Select a random power-up type
        PowerUp.PowerUpType type = types[random.nextInt(types.length)];
        // X-coordinate: between the two player starting areas (middle half of the screen)
        double x = GAME_WIDTH / 4 + random.nextDouble() * (GAME_WIDTH / 2);
        // Y-coordinate: anywhere vertically within game bounds, considering power-up size
        double y = random.nextDouble() * (GAME_HEIGHT - POWERUP_SIZE);
        // Duration: random between 5 to 10 seconds
        long duration = 5000 + random.nextInt(5000);

        gameState.getPowerUps().add(new PowerUp(type, x, y, duration));
    }

    /**
     * Helper method to find a Player object by their name from the current game state.
     * @param playerName The name of the player to find.
     * @return The Player object if found, null otherwise.
     */
    private Player getPlayer(String playerName) {
        for (Player player : gameState.getPlayers()) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Saves the final scores of both players to the leaderboard database.
     * This is called once the game concludes.
     */
    public void saveGameResults() {
        // Ensure there are two players whose scores can be saved
        if (gameState.getPlayers().size() == 2) {
            Player player1 = gameState.getPlayers().get(0);
            Player player2 = gameState.getPlayers().get(1);

            Timestamp matchDate = new Timestamp(System.currentTimeMillis());

            // Save both players' scores, regardless of who won or if it was a draw
            leaderboardRepository.save(new Leaderboard(player1.getName(), player1.getScore(), matchDate));
            leaderboardRepository.save(new Leaderboard(player2.getName(), player2.getScore(), matchDate));
        }
    }
}
