package com.cricket.score.dto;

import com.cricket.score.model.DismissalType;
import com.cricket.score.model.ExtraType;

public class BallEventRequest {
    private Integer runsScored = 0;
    private ExtraType extraType = ExtraType.NONE;
    private Integer extraRuns = 0;
    private Boolean isWicket = false;
    private DismissalType dismissalType = DismissalType.NONE;
    private Long dismissedPlayerId;
    private Long strikerId;
    private Long nonStrikerId;
    private Long bowlerId;
    private String commentary;

    public BallEventRequest() {}

    public Integer getRunsScored() { return runsScored; }
    public void setRunsScored(Integer runsScored) { this.runsScored = runsScored; }

    public ExtraType getExtraType() { return extraType; }
    public void setExtraType(ExtraType extraType) { this.extraType = extraType; }

    public Integer getExtraRuns() { return extraRuns; }
    public void setExtraRuns(Integer extraRuns) { this.extraRuns = extraRuns; }

    public Boolean getIsWicket() { return isWicket; }
    public void setIsWicket(Boolean wicket) { isWicket = wicket; }

    public DismissalType getDismissalType() { return dismissalType; }
    public void setDismissalType(DismissalType dismissalType) { this.dismissalType = dismissalType; }

    public Long getDismissedPlayerId() { return dismissedPlayerId; }
    public void setDismissedPlayerId(Long dismissedPlayerId) { this.dismissedPlayerId = dismissedPlayerId; }

    public Long getStrikerId() { return strikerId; }
    public void setStrikerId(Long strikerId) { this.strikerId = strikerId; }

    public Long getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(Long nonStrikerId) { this.nonStrikerId = nonStrikerId; }

    public Long getBowlerId() { return bowlerId; }
    public void setBowlerId(Long bowlerId) { this.bowlerId = bowlerId; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }
}
