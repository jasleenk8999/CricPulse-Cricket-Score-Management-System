package com.cricket.score.controller;

import com.cricket.score.model.Player;
import com.cricket.score.service.TeamPlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Player Management", description = "APIs for maintaining player information and team rosters")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final TeamPlayerService teamPlayerService;

    public PlayerController(TeamPlayerService teamPlayerService) {
        this.teamPlayerService = teamPlayerService;
    }

    @GetMapping
    @Operation(summary = "Get players", description = "Lists players, optionally filtered by team ID.")
    public ResponseEntity<List<Player>> getPlayers(@RequestParam(required = false) Long teamId) {
        if (teamId != null) {
            return ResponseEntity.ok(teamPlayerService.getPlayersByTeamId(teamId));
        }
        return ResponseEntity.ok(teamPlayerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get player by ID", description = "Fetches details of a specific player.")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(teamPlayerService.getPlayerById(id));
    }

    @PostMapping
    @Operation(summary = "Add a new player", description = "Registers a new player under a specified team.")
    public ResponseEntity<Player> addPlayer(@RequestBody Player player, @RequestParam Long teamId) {
        Player created = teamPlayerService.addPlayer(player, teamId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
