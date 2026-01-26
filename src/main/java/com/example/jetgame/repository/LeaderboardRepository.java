package com.example.jetgame.repository;

import com.example.jetgame.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link Leaderboard} entity.
 * Provides standard CRUD operations and custom query methods for accessing leaderboard data.
 */
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    /**
     * Retrieves the top 10 {@link Leaderboard} entries, ordered by score in descending order.
     * Spring Data JPA automatically generates the query for this method based on its name.
     * @return A list of the top 10 leaderboard entries.
     */
    List<Leaderboard> findTop10ByOrderByScoreDesc();
}
