package com.example.jetgame.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a single entry in the game's leaderboard.
 * This class is a JPA entity, mapping to the 'leaderboard' table in the database.
 */
@Entity
@Table(name = "leaderboard") // Specifies the table name in the database
public class Leaderboard {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures auto-increment for the primary key
    private Long id;

    @Column(name = "player_name") // Maps the field to the 'player_name' column
    private String playerName;

    @Column(name = "score") // Maps the field to the 'score' column
    private int score;

    @Column(name = "match_date") // Maps the field to the 'match_date' column
    private Timestamp matchDate;

    /**
     * Default constructor required by JPA.
     */
    public Leaderboard() {
    }

    /**
     * Constructor to create a new Leaderboard entry.
     * @param playerName The name of the player.
     * @param score The score achieved in the match.
     * @param matchDate The timestamp when the match was played.
     */
    public Leaderboard(String playerName, int score, Timestamp matchDate) {
        this.playerName = playerName;
        this.score = score;
        this.matchDate = matchDate;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }




    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }



    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }



    public Timestamp getMatchDate() {
        return matchDate;
    }
    public void setMatchDate(Timestamp matchDate) {
        this.matchDate = matchDate;
    }
}
