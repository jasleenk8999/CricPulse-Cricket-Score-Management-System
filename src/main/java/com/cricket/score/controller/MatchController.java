package com.cricket.score.controller;

import com.cricket.score.dto.BallEventRequest;
import com.cricket.score.dto.MatchCreateRequest;
import com.cricket.score.dto.MatchSummaryResponse;
import com.cricket.score.dto.ScorecardResponse;
import com.cricket.score.model.BallEvent;
import com.cricket.score.model.Match;
import com.cricket.score.service.MatchService;
import com.cricket.score.service.ScoringService;
import com.cricket.score.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Match & Score Management", description = "APIs for creating, tracking, scoring, and simulating cricket matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;
    private final ScoringService scoringService;
    private final SimulationService simulationService;

    public MatchController(MatchService matchService,
                           ScoringService scoringService,
                           SimulationService simulationService) {
        this.matchService = matchService;
        this.scoringService = scoringService;
        this.simulationService = simulationService;
    }

    @GetMapping
    @Operation(summary = "Get all matches", description = "Retrieves a list of all matches in order of latest created.")
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/summaries")
    @Operation(summary = "Get match summaries", description = "Retrieves concise summaries and scores for all ongoing and past matches.")
    public ResponseEntity<List<MatchSummaryResponse>> getMatchSummaries() {
        return ResponseEntity.ok(matchService.getMatchSummaries());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get match details by ID", description = "Fetches match information by match ID.")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new match", description = "Schedules and creates a new cricket match between two teams.")
    public ResponseEntity<Match> createMatch(@RequestBody MatchCreateRequest request) {
        Match created = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/scorecard")
    @Operation(summary = "Get live scorecard & player statistics", description = "Returns live scorecard, active batsman/bowler stats, and recent ball commentary.")
    public ResponseEntity<ScorecardResponse> getScorecard(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getScorecard(id));
    }

    @PostMapping("/{id}/ball")
    @Operation(summary = "Record a ball event", description = "Records a single ball event (runs, wickets, extras) and updates live scores and player stats.")
    public ResponseEntity<BallEvent> recordBall(@PathVariable Long id, @RequestBody BallEventRequest request) {
        BallEvent ball = scoringService.recordBall(id, request);
        return ResponseEntity.ok(ball);
    }

    @PostMapping("/{id}/simulate-ball")
    @Operation(summary = "Auto simulate single ball", description = "Simulates one ball with realistic randomized events and commentary.")
    public ResponseEntity<BallEvent> simulateBall(@PathVariable Long id) {
        BallEvent ball = simulationService.simulateBall(id);
        return ResponseEntity.ok(ball);
    }

    @PostMapping("/{id}/simulate-match")
    @Operation(summary = "Auto simulate entire match", description = "Runs continuous simulation until match completion.")
    public ResponseEntity<Match> simulateMatch(@PathVariable Long id) {
        Match match = simulationService.simulateMatch(id);
        return ResponseEntity.ok(match);
    }
}
