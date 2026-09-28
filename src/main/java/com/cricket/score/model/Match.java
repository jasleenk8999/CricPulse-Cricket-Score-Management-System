package com.cricket.score.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private MatchFormat matchFormat;

    private String venue;
    private Integer totalOvers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_a_id")
    private Team teamA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_b_id")
    private Team teamB;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "toss_winner_id")
    private Team tossWinner;

    @Enumerated(EnumType.STRING)
    private TossDecision tossDecision;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private Integer currentInningsNumber = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    private String resultSummary;
    private LocalDateTime matchDate = LocalDateTime.now();

    public Match() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public MatchFormat getMatchFormat() { return matchFormat; }
    public void setMatchFormat(MatchFormat matchFormat) { this.matchFormat = matchFormat; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getTotalOvers() { return totalOvers; }
    public void setTotalOvers(Integer totalOvers) { this.totalOvers = totalOvers; }

    public Team getTeamA() { return teamA; }
    public void setTeamA(Team teamA) { this.teamA = teamA; }

    public Team getTeamB() { return teamB; }
    public void setTeamB(Team teamB) { this.teamB = teamB; }

    public Team getTossWinner() { return tossWinner; }
    public void setTossWinner(Team tossWinner) { this.tossWinner = tossWinner; }

    public TossDecision getTossDecision() { return tossDecision; }
    public void setTossDecision(TossDecision tossDecision) { this.tossDecision = tossDecision; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Integer getCurrentInningsNumber() { return currentInningsNumber; }
    public void setCurrentInningsNumber(Integer currentInningsNumber) { this.currentInningsNumber = currentInningsNumber; }

    public Team getWinnerTeam() { return winnerTeam; }
    public void setWinnerTeam(Team winnerTeam) { this.winnerTeam = winnerTeam; }

    public String getResultSummary() { return resultSummary; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }

    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
}
