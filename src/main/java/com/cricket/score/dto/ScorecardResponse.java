package com.cricket.score.dto;

import com.cricket.score.model.BallEvent;
import com.cricket.score.model.Innings;
import com.cricket.score.model.Match;
import com.cricket.score.model.PlayerMatchStat;

import java.util.List;

public class ScorecardResponse {
    private Match match;
    private Innings currentInnings;
    private List<Innings> allInnings;
    private List<PlayerMatchStat> battingStats;
    private List<PlayerMatchStat> bowlingStats;
    private List<BallEvent> recentBalls;
    private PlayerMatchStat currentStrikerStat;
    private PlayerMatchStat currentNonStrikerStat;
    private PlayerMatchStat currentBowlerStat;

    public ScorecardResponse() {}

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Innings getCurrentInnings() { return currentInnings; }
    public void setCurrentInnings(Innings currentInnings) { this.currentInnings = currentInnings; }

    public List<Innings> getAllInnings() { return allInnings; }
    public void setAllInnings(List<Innings> allInnings) { this.allInnings = allInnings; }

    public List<PlayerMatchStat> getBattingStats() { return battingStats; }
    public void setBattingStats(List<PlayerMatchStat> battingStats) { this.battingStats = battingStats; }

    public List<PlayerMatchStat> getBowlingStats() { return bowlingStats; }
    public void setBowlingStats(List<PlayerMatchStat> bowlingStats) { this.bowlingStats = bowlingStats; }

    public List<BallEvent> getRecentBalls() { return recentBalls; }
    public void setRecentBalls(List<BallEvent> recentBalls) { this.recentBalls = recentBalls; }

    public PlayerMatchStat getCurrentStrikerStat() { return currentStrikerStat; }
    public void setCurrentStrikerStat(PlayerMatchStat currentStrikerStat) { this.currentStrikerStat = currentStrikerStat; }

    public PlayerMatchStat getCurrentNonStrikerStat() { return currentNonStrikerStat; }
    public void setCurrentNonStrikerStat(PlayerMatchStat currentNonStrikerStat) { this.currentNonStrikerStat = currentNonStrikerStat; }

    public PlayerMatchStat getCurrentBowlerStat() { return currentBowlerStat; }
    public void setCurrentBowlerStat(PlayerMatchStat currentBowlerStat) { this.currentBowlerStat = currentBowlerStat; }
}
