package com.cricket.score.dto;

import com.cricket.score.model.MatchStatus;

public class MatchSummaryResponse {
    private Long matchId;
    private String title;
    private MatchStatus status;
    private String venue;
    private String teamAName;
    private String teamAShortName;
    private String teamBName;
    private String teamBShortName;
    private String firstInningsScore;
    private String secondInningsScore;
    private String resultSummary;

    public MatchSummaryResponse() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getTeamAName() { return teamAName; }
    public void setTeamAName(String teamAName) { this.teamAName = teamAName; }

    public String getTeamAShortName() { return teamAShortName; }
    public void setTeamAShortName(String teamAShortName) { this.teamAShortName = teamAShortName; }

    public String getTeamBName() { return teamBName; }
    public void setTeamBName(String teamBName) { this.teamBName = teamBName; }

    public String getTeamBShortName() { return teamBShortName; }
    public void setTeamBShortName(String teamBShortName) { this.teamBShortName = teamBShortName; }

    public String getFirstInningsScore() { return firstInningsScore; }
    public void setFirstInningsScore(String firstInningsScore) { this.firstInningsScore = firstInningsScore; }

    public String getSecondInningsScore() { return secondInningsScore; }
    public void setSecondInningsScore(String secondInningsScore) { this.secondInningsScore = secondInningsScore; }

    public String getResultSummary() { return resultSummary; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
}
