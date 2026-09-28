package com.cricket.score.service;

import com.cricket.score.dto.BallEventRequest;
import com.cricket.score.model.*;
import com.cricket.score.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SimulationService {

    private final ScoringService scoringService;
    private final MatchRepository matchRepository;
    private final Random random = new Random();

    public SimulationService(ScoringService scoringService, MatchRepository matchRepository) {
        this.scoringService = scoringService;
        this.matchRepository = matchRepository;
    }

    public BallEvent simulateBall(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Match is already completed!");
        }

        BallEventRequest request = new BallEventRequest();
        int roll = random.nextInt(100);

        if (roll < 35) {
            // Dot ball (0 runs)
            request.setRunsScored(0);
        } else if (roll < 60) {
            // 1 run
            request.setRunsScored(1);
        } else if (roll < 72) {
            // 2 runs
            request.setRunsScored(2);
        } else if (roll < 84) {
            // 4 runs (Boundary)
            request.setRunsScored(4);
        } else if (roll < 91) {
            // 6 runs (Sixer)
            request.setRunsScored(6);
        } else if (roll < 95) {
            // Extra: Wide or No Ball
            if (random.nextBoolean()) {
                request.setExtraType(ExtraType.WIDE);
            } else {
                request.setExtraType(ExtraType.NO_BALL);
                request.setRunsScored(random.nextBoolean() ? 1 : 0);
            }
        } else {
            // Wicket!
            request.setIsWicket(true);
            DismissalType[] dismissals = {DismissalType.BOWLED, DismissalType.CAUGHT, DismissalType.LBW, DismissalType.RUN_OUT, DismissalType.STUMPED};
            request.setDismissalType(dismissals[random.nextInt(dismissals.length)]);
        }

        return scoringService.recordBall(matchId, request);
    }

    public Match simulateMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        int maxLoop = 300; // safety ceiling
        int count = 0;
        while (match.getStatus() != MatchStatus.COMPLETED && count < maxLoop) {
            simulateBall(matchId);
            match = matchRepository.findById(matchId).orElse(match);
            count++;
        }
        return match;
    }
}
