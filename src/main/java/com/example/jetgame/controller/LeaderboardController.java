package com.example.jetgame.controller;

import com.example.jetgame.model.Leaderboard;
import com.example.jetgame.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing the game leaderboard.
 * Provides endpoints for saving new scores and retrieving the top scores.
 */
@RestController
@RequestMapping("/api/leaderboard") // Base URL for leaderboard API endpoints
public class LeaderboardController {

    private final LeaderboardRepository leaderboardRepository;

    /**
     * Injects the LeaderboardRepository for database interactions.
     * @param leaderboardRepository The repository for accessing leaderboard data.
     */
    @Autowired
    public LeaderboardController(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    /**
     * Saves a new score entry to the leaderboard.
     * This endpoint is typically called by the backend GameService when a game concludes.
     * @param leaderboard The Leaderboard object containing player name, score, and match date.
     * @return The saved Leaderboard object.
     */
    @PostMapping
    public Leaderboard saveScore(@RequestBody Leaderboard leaderboard) {
        return leaderboardRepository.save(leaderboard);
    }

    /**
     * Retrieves the top 10 scores from the leaderboard, sorted by score in descending order.
     * This endpoint is called by the frontend to display the scoreboard.
     * @return A list of the top 10 Leaderboard entries.
     */
    @GetMapping
    public List<Leaderboard> getLeaderboard() {
        return leaderboardRepository.findTop10ByOrderByScoreDesc();
    }
}
