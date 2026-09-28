package com.cricket.score.repository;

import com.cricket.score.model.BallEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BallEventRepository extends JpaRepository<BallEvent, Long> {
    List<BallEvent> findByMatchIdOrderByIdDesc(Long matchId);
    List<BallEvent> findByInningsIdOrderByIdAsc(Long inningsId);
}
