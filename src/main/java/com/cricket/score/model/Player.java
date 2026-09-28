package com.cricket.score.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String battingStyle;
    private String bowlingStyle;
    private Integer jerseyNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties("players")
    private Team team;

    public Player() {}

    public Player(String name, Role role, String battingStyle, String bowlingStyle, Integer jerseyNumber, Team team) {
        this.name = name;
        this.role = role;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
        this.jerseyNumber = jerseyNumber;
        this.team = team;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getBattingStyle() { return battingStyle; }
    public void setBattingStyle(String battingStyle) { this.battingStyle = battingStyle; }

    public String getBowlingStyle() { return bowlingStyle; }
    public void setBowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; }

    public Integer getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(Integer jerseyNumber) { this.jerseyNumber = jerseyNumber; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
}
