package com.cricket.score.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ball_events")
public class BallEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;

    private Integer overNumber; // e.g. 0 for 1st over, 1 for 2nd over
    private Integer ballNumber; // 1..6 or extra ball

    @ManyToOne
    @JoinColumn(name = "striker_id")
    private Player striker;

    @ManyToOne
    @JoinColumn(name = "non_striker_id")
    private Player nonStriker;

    @ManyToOne
    @JoinColumn(name = "bowler_id")
    private Player bowler;

    private Integer runsScored = 0;

    @Enumerated(EnumType.STRING)
    private ExtraType extraType = ExtraType.NONE;

    private Integer extraRuns = 0;

    private Boolean isWicket = false;

    @Enumerated(EnumType.STRING)
    private DismissalType dismissalType = DismissalType.NONE;

    @ManyToOne
    @JoinColumn(name = "dismissed_player_id")
    private Player dismissedPlayer;

    @Column(length = 500)
    private String commentary;

    private LocalDateTime timestamp = LocalDateTime.now();

    public BallEvent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Innings getInnings() { return innings; }
    public void setInnings(Innings innings) { this.innings = innings; }

    public Integer getOverNumber() { return overNumber; }
    public void setOverNumber(Integer overNumber) { this.overNumber = overNumber; }

    public Integer getBallNumber() { return ballNumber; }
    public void setBallNumber(Integer ballNumber) { this.ballNumber = ballNumber; }

    public Player getStriker() { return striker; }
    public void setStriker(Player striker) { this.striker = striker; }

    public Player getNonStriker() { return nonStriker; }
    public void setNonStriker(Player nonStriker) { this.nonStriker = nonStriker; }

    public Player getBowler() { return bowler; }
    public void setBowler(Player bowler) { this.bowler = bowler; }

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

    public Player getDismissedPlayer() { return dismissedPlayer; }
    public void setDismissedPlayer(Player dismissedPlayer) { this.dismissedPlayer = dismissedPlayer; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
