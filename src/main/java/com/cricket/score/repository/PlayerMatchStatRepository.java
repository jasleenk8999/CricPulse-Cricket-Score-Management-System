package com.cricket.score.repository;

import com.cricket.score.model.PlayerMatchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerMatchStatRepository extends JpaRepository<PlayerMatchStat, Long> {
    List<PlayerMatchStat> findByMatchId(Long matchId);
    List<PlayerMatchStat> findByInningsId(Long inningsId);
    Optional<PlayerMatchStat> findByInningsIdAndPlayerId(Long inningsId, Long playerId);
}
