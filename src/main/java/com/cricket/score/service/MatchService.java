package com.cricket.score.service;

import com.cricket.score.dto.MatchCreateRequest;
import com.cricket.score.dto.MatchSummaryResponse;
import com.cricket.score.dto.ScorecardResponse;
import com.cricket.score.model.*;
import com.cricket.score.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final InningsRepository inningsRepository;
    private final PlayerMatchStatRepository playerMatchStatRepository;
    private final BallEventRepository ballEventRepository;
    private final ScoringService scoringService;

    public MatchService(MatchRepository matchRepository,
                        TeamRepository teamRepository,
                        InningsRepository inningsRepository,
                        PlayerMatchStatRepository playerMatchStatRepository,
                        BallEventRepository ballEventRepository,
                        ScoringService scoringService) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.inningsRepository = inningsRepository;
        this.playerMatchStatRepository = playerMatchStatRepository;
        this.ballEventRepository = ballEventRepository;
        this.scoringService = scoringService;
    }

    @Transactional
    public Match createMatch(MatchCreateRequest request) {
        Team teamA = teamRepository.findById(request.getTeamAId())
                .orElseThrow(() -> new IllegalArgumentException("Team A not found: " + request.getTeamAId()));
        Team teamB = teamRepository.findById(request.getTeamBId())
                .orElseThrow(() -> new IllegalArgumentException("Team B not found: " + request.getTeamBId()));
        Team tossWinner = teamRepository.findById(request.getTossWinnerId())
                .orElseThrow(() -> new IllegalArgumentException("Toss Winner not found: " + request.getTossWinnerId()));

        Match match = new Match();
        match.setTitle(request.getTitle());
        match.setMatchFormat(request.getMatchFormat() != null ? request.getMatchFormat() : MatchFormat.T20);
        match.setVenue(request.getVenue());
        match.setTotalOvers(request.getTotalOvers() != null ? request.getTotalOvers() : 20);
        match.setTeamA(teamA);
        match.setTeamB(teamB);
        match.setTossWinner(tossWinner);
        match.setTossDecision(request.getTossDecision());
        match.setStatus(MatchStatus.LIVE);
        match.setCurrentInningsNumber(1);
        match.setMatchDate(LocalDateTime.now());

        Match savedMatch = matchRepository.save(match);

        // Determine batting and bowling team for 1st innings
        Team battingTeam = (request.getTossDecision() == TossDecision.BAT) ? tossWinner :
                (tossWinner.getId().equals(teamA.getId()) ? teamB : teamA);
        Team bowlingTeam = (battingTeam.getId().equals(teamA.getId())) ? teamB : teamA;

        Innings firstInnings = new Innings();
        firstInnings.setMatch(savedMatch);
        firstInnings.setInningsNumber(1);
        firstInnings.setBattingTeam(battingTeam);
        firstInnings.setBowlingTeam(bowlingTeam);
        scoringService.initializeInningsPlayers(firstInnings);

        inningsRepository.save(firstInnings);

        return savedMatch;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAllByOrderByIdDesc();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + id));
    }

    public ScorecardResponse getScorecard(Long matchId) {
        Match match = getMatchById(matchId);
        List<Innings> allInnings = inningsRepository.findByMatchId(matchId);
        Innings currentInnings = inningsRepository.findByMatchIdAndInningsNumber(matchId, match.getCurrentInningsNumber())
                .orElse(allInnings.isEmpty() ? null : allInnings.get(allInnings.size() - 1));

        ScorecardResponse response = new ScorecardResponse();
        response.setMatch(match);
        response.setCurrentInnings(currentInnings);
        response.setAllInnings(allInnings);

        if (currentInnings != null) {
            List<PlayerMatchStat> stats = playerMatchStatRepository.findByInningsId(currentInnings.getId());
            
            // Batting stats (players who faced balls or are current strikers/non-strikers)
            List<PlayerMatchStat> battingStats = stats.stream()
                    .filter(s -> s.getBallsFaced() > 0 || s.getIsOut() || 
                            (currentInnings.getCurrentStriker() != null && s.getPlayer().getId().equals(currentInnings.getCurrentStriker().getId())) ||
                            (currentInnings.getCurrentNonStriker() != null && s.getPlayer().getId().equals(currentInnings.getCurrentNonStriker().getId())))
                    .toList();

            // Bowling stats (players who bowled legal balls or extras)
            List<PlayerMatchStat> bowlingStats = stats.stream()
                    .filter(s -> s.getOversBowledLegalBalls() > 0 || s.getWideBallsBowled() > 0 || s.getNoBallsBowled() > 0)
                    .toList();

            response.setBattingStats(battingStats);
            response.setBowlingStats(bowlingStats);

            if (currentInnings.getCurrentStriker() != null) {
                response.setCurrentStrikerStat(playerMatchStatRepository
                        .findByInningsIdAndPlayerId(currentInnings.getId(), currentInnings.getCurrentStriker().getId()).orElse(null));
            }
            if (currentInnings.getCurrentNonStriker() != null) {
                response.setCurrentNonStrikerStat(playerMatchStatRepository
                        .findByInningsIdAndPlayerId(currentInnings.getId(), currentInnings.getCurrentNonStriker().getId()).orElse(null));
            }
            if (currentInnings.getCurrentBowler() != null) {
                response.setCurrentBowlerStat(playerMatchStatRepository
                        .findByInningsIdAndPlayerId(currentInnings.getId(), currentInnings.getCurrentBowler().getId()).orElse(null));
            }

            List<BallEvent> recentBalls = ballEventRepository.findByMatchIdOrderByIdDesc(matchId);
            response.setRecentBalls(recentBalls.size() > 15 ? recentBalls.subList(0, 15) : recentBalls);
        }

        return response;
    }

    public List<MatchSummaryResponse> getMatchSummaries() {
        List<Match> matches = matchRepository.findAllByOrderByIdDesc();
        List<MatchSummaryResponse> list = new ArrayList<>();

        for (Match m : matches) {
            MatchSummaryResponse dto = new MatchSummaryResponse();
            dto.setMatchId(m.getId());
            dto.setTitle(m.getTitle());
            dto.setStatus(m.getStatus());
            dto.setVenue(m.getVenue());
            dto.setTeamAName(m.getTeamA().getName());
            dto.setTeamAShortName(m.getTeamA().getShortName());
            dto.setTeamBName(m.getTeamB().getName());
            dto.setTeamBShortName(m.getTeamB().getShortName());
            dto.setResultSummary(m.getResultSummary());

            List<Innings> inningsList = inningsRepository.findByMatchId(m.getId());
            if (!inningsList.isEmpty()) {
                Innings inn1 = inningsList.get(0);
                dto.setFirstInningsScore(inn1.getBattingTeam().getShortName() + " " + inn1.getTotalRuns() + "/" + inn1.getTotalWickets() + " (" + inn1.getOversFormatted() + " ov)");
            }
            if (inningsList.size() > 1) {
                Innings inn2 = inningsList.get(1);
                dto.setSecondInningsScore(inn2.getBattingTeam().getShortName() + " " + inn2.getTotalRuns() + "/" + inn2.getTotalWickets() + " (" + inn2.getOversFormatted() + " ov)");
            }

            list.add(dto);
        }
        return list;
    }
}
