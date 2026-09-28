package com.cricket.score.controller;

import com.cricket.score.model.Team;
import com.cricket.score.service.TeamPlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Team Management", description = "APIs for maintaining team information")
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamPlayerService teamPlayerService;

    public TeamController(TeamPlayerService teamPlayerService) {
        this.teamPlayerService = teamPlayerService;
    }

    @GetMapping
    @Operation(summary = "Get all teams", description = "Lists all registered teams in the system.")
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamPlayerService.getAllTeams());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID", description = "Fetches team details along with player roster.")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamPlayerService.getTeamById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new team", description = "Registers a new team into the platform.")
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team created = teamPlayerService.createTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
