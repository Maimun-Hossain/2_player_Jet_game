package com.example.jetgame.controller;

import com.example.jetgame.model.websocket.GameMessage;
import com.example.jetgame.model.websocket.MessageType;
import com.example.jetgame.model.websocket.PlayerAction;
import com.example.jetgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles incoming WebSocket messages for game actions and manages the game loop.
 * Messages are mapped from client destinations (e.g., "/app/join") to methods here.
 * Game state updates are sent back to clients via the message broker.
 */
@Controller
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private ScheduledExecutorService executorService; // Manages the game update loop

    /**
     * Injects the GameService and SimpMessagingTemplate for game logic and sending messages.
     * @param gameService The core game logic service.
     * @param messagingTemplate Used to send messages to WebSocket clients.
     */
    @Autowired
    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handles player join requests.
     * When a player joins, their name is added to the game service. If it's the first player,
     * a waiting message is sent. If it's the second player, the game loop starts and
     * a GAME_START message with the initial game state is sent to all clients.
     * @param playerName The name of the player joining.
     */
    @MessageMapping("/join")
    public void join(String playerName) {
        gameService.addPlayer(playerName);
        if (gameService.getGameState().getPlayers().size() == 1) {
            // First player joined, waiting for another
            messagingTemplate.convertAndSend("/topic/game", new GameMessage(MessageType.WAITING_FOR_PLAYER, playerName));
        } else if (gameService.getGameState().getPlayers().size() == 2) {
            // Second player joined, start the game
            startGameLoop();
            messagingTemplate.convertAndSend("/topic/game", new GameMessage(MessageType.GAME_START, gameService.getGameState()));
        }
    }

    /**
     * Handles player actions (move, shoot).
     * Actions are processed by the GameService. If the game is over, no actions are processed.
     * @param action The PlayerAction object containing player name and action type.
     */
    @MessageMapping("/action")
    public void handleAction(PlayerAction action) {
        if (gameService.isGameOver()) {
            // Do not process actions if the game is over
            return;
        }

        if (action.getAction().equals("UP") || action.getAction().equals("DOWN")) {
            gameService.movePlayer(action.getPlayer(), action.getAction());
        } else if ("SHOOT".equals(action.getAction())) {
            gameService.shoot(action.getPlayer());
        }
    }

    /**
     * Initializes and starts the game update loop.
     * This scheduled task periodically updates the game state via GameService and
     * broadcasts the updates to all connected clients.
     */
    private void startGameLoop() {
        // Ensure any previous game loop is shut down to prevent multiple loops running concurrently
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        // Create a new single-threaded scheduled executor for the game loop
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            // If the game is over, send a GAME_OVER message and stop the loop
            if (gameService.isGameOver()) {
                messagingTemplate.convertAndSend("/topic/game", new GameMessage(MessageType.GAME_OVER, gameService.getGameState()));
                executorService.shutdown(); // Stop the game loop
            } else {
                // Otherwise, update the game state and send a SCORE_UPDATE message
                gameService.update();
                messagingTemplate.convertAndSend("/topic/game", new GameMessage(MessageType.SCORE_UPDATE, gameService.getGameState()));
            }
        }, 0, 16, TimeUnit.MILLISECONDS); // Runs approximately 60 times per second (~60 FPS)
    }
}
