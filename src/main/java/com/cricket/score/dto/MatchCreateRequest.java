package com.cricket.score.dto;

import com.cricket.score.model.MatchFormat;
import com.cricket.score.model.TossDecision;

public class MatchCreateRequest {
    private String title;
    private MatchFormat matchFormat;
    private String venue;
    private Integer totalOvers;
    private Long teamAId;
    private Long teamBId;
    private Long tossWinnerId;
    private TossDecision tossDecision;

    public MatchCreateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public MatchFormat getMatchFormat() { return matchFormat; }
    public void setMatchFormat(MatchFormat matchFormat) { this.matchFormat = matchFormat; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getTotalOvers() { return totalOvers; }
    public void setTotalOvers(Integer totalOvers) { this.totalOvers = totalOvers; }

    public Long getTeamAId() { return teamAId; }
    public void setTeamAId(Long teamAId) { this.teamAId = teamAId; }

    public Long getTeamBId() { return teamBId; }
    public void setTeamBId(Long teamBId) { this.teamBId = teamBId; }

    public Long getTossWinnerId() { return tossWinnerId; }
    public void setTossWinnerId(Long tossWinnerId) { this.tossWinnerId = tossWinnerId; }

    public TossDecision getTossDecision() { return tossDecision; }
    public void setTossDecision(TossDecision tossDecision) { this.tossDecision = tossDecision; }
}
