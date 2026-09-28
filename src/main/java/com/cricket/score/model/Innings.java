package com.cricket.score.model;

import jakarta.persistence.*;

@Entity
@Table(name = "innings")
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    private Integer inningsNumber; // 1 or 2

    @ManyToOne
    @JoinColumn(name = "batting_team_id")
    private Team battingTeam;

    @ManyToOne
    @JoinColumn(name = "bowling_team_id")
    private Team bowlingTeam;

    private Integer totalRuns = 0;
    private Integer totalWickets = 0;
    private Integer completedOvers = 0;
    private Integer ballsInCurrentOver = 0;

    private Integer wides = 0;
    private Integer noBalls = 0;
    private Integer byes = 0;
    private Integer legByes = 0;

    private Integer targetRuns; // for 2nd innings

    @ManyToOne
    @JoinColumn(name = "current_striker_id")
    private Player currentStriker;

    @ManyToOne
    @JoinColumn(name = "current_non_striker_id")
    private Player currentNonStriker;

    @ManyToOne
    @JoinColumn(name = "current_bowler_id")
    private Player currentBowler;

    private Boolean isCompleted = false;

    public Innings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Integer getInningsNumber() { return inningsNumber; }
    public void setInningsNumber(Integer inningsNumber) { this.inningsNumber = inningsNumber; }

    public Team getBattingTeam() { return battingTeam; }
    public void setBattingTeam(Team battingTeam) { this.battingTeam = battingTeam; }

    public Team getBowlingTeam() { return bowlingTeam; }
    public void setBowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; }

    public Integer getTotalRuns() { return totalRuns; }
    public void setTotalRuns(Integer totalRuns) { this.totalRuns = totalRuns; }

    public Integer getTotalWickets() { return totalWickets; }
    public void setTotalWickets(Integer totalWickets) { this.totalWickets = totalWickets; }

    public Integer getCompletedOvers() { return completedOvers; }
    public void setCompletedOvers(Integer completedOvers) { this.completedOvers = completedOvers; }

    public Integer getBallsInCurrentOver() { return ballsInCurrentOver; }
    public void setBallsInCurrentOver(Integer ballsInCurrentOver) { this.ballsInCurrentOver = ballsInCurrentOver; }

    public Integer getWides() { return wides; }
    public void setWides(Integer wides) { this.wides = wides; }

    public Integer getNoBalls() { return noBalls; }
    public void setNoBalls(Integer noBalls) { this.noBalls = noBalls; }

    public Integer getByes() { return byes; }
    public void setByes(Integer byes) { this.byes = byes; }

    public Integer getLegByes() { return legByes; }
    public void setLegByes(Integer legByes) { this.legByes = legByes; }

    public Integer getTargetRuns() { return targetRuns; }
    public void setTargetRuns(Integer targetRuns) { this.targetRuns = targetRuns; }

    public Player getCurrentStriker() { return currentStriker; }
    public void setCurrentStriker(Player currentStriker) { this.currentStriker = currentStriker; }

    public Player getCurrentNonStriker() { return currentNonStriker; }
    public void setCurrentNonStriker(Player currentNonStriker) { this.currentNonStriker = currentNonStriker; }

    public Player getCurrentBowler() { return currentBowler; }
    public void setCurrentBowler(Player currentBowler) { this.currentBowler = currentBowler; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }

    public double getOversFormatted() {
        return completedOvers + (ballsInCurrentOver / 10.0);
    }

    public double getRunRate() {
        double totalOversDec = completedOvers + (ballsInCurrentOver / 6.0);
        if (totalOversDec == 0) return 0.0;
        return Math.round((totalRuns / totalOversDec) * 100.0) / 100.0;
    }
}
