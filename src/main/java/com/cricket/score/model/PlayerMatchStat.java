package com.cricket.score.model;

import jakarta.persistence.*;

@Entity
@Table(name = "player_match_stats")
public class PlayerMatchStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    // Batting stats
    private Integer runsScored = 0;
    private Integer ballsFaced = 0;
    private Integer fours = 0;
    private Integer sixes = 0;
    private Boolean isOut = false;
    private String dismissalInfo; // e.g. "b Starc" or "c Maxwell b Zampa" or "not out"

    // Bowling stats
    private Integer oversBowledLegalBalls = 0; // count of legal balls bowled
    private Integer maidenOvers = 0;
    private Integer runsConceded = 0;
    private Integer wicketsTaken = 0;
    private Integer wideBallsBowled = 0;
    private Integer noBallsBowled = 0;

    public PlayerMatchStat() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Innings getInnings() { return innings; }
    public void setInnings(Innings innings) { this.innings = innings; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Integer getRunsScored() { return runsScored; }
    public void setRunsScored(Integer runsScored) { this.runsScored = runsScored; }

    public Integer getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(Integer ballsFaced) { this.ballsFaced = ballsFaced; }

    public Integer getFours() { return fours; }
    public void setFours(Integer fours) { this.fours = fours; }

    public Integer getSixes() { return sixes; }
    public void setSixes(Integer sixes) { this.sixes = sixes; }

    public Boolean getIsOut() { return isOut; }
    public void setIsOut(Boolean isOut) { this.isOut = isOut; }

    public String getDismissalInfo() { return dismissalInfo; }
    public void setDismissalInfo(String dismissalInfo) { this.dismissalInfo = dismissalInfo; }

    public Integer getOversBowledLegalBalls() { return oversBowledLegalBalls; }
    public void setOversBowledLegalBalls(Integer oversBowledLegalBalls) { this.oversBowledLegalBalls = oversBowledLegalBalls; }

    public Integer getMaidenOvers() { return maidenOvers; }
    public void setMaidenOvers(Integer maidenOvers) { this.maidenOvers = maidenOvers; }

    public Integer getRunsConceded() { return runsConceded; }
    public void setRunsConceded(Integer runsConceded) { this.runsConceded = runsConceded; }

    public Integer getWicketsTaken() { return wicketsTaken; }
    public void setWicketsTaken(Integer wicketsTaken) { this.wicketsTaken = wicketsTaken; }

    public Integer getWideBallsBowled() { return wideBallsBowled; }
    public void setWideBallsBowled(Integer wideBallsBowled) { this.wideBallsBowled = wideBallsBowled; }

    public Integer getNoBallsBowled() { return noBallsBowled; }
    public void setNoBallsBowled(Integer noBallsBowled) { this.noBallsBowled = noBallsBowled; }

    public double getStrikeRate() {
        if (ballsFaced == 0) return 0.0;
        return Math.round((runsScored * 100.0 / ballsFaced) * 100.0) / 100.0;
    }

    public String getOversBowledFormatted() {
        int fullOvers = oversBowledLegalBalls / 6;
        int remBalls = oversBowledLegalBalls % 6;
        return fullOvers + "." + remBalls;
    }

    public double getEconomyRate() {
        double oversDec = oversBowledLegalBalls / 6.0;
        if (oversDec == 0) return 0.0;
        return Math.round((runsConceded / oversDec) * 100.0) / 100.0;
    }
}
