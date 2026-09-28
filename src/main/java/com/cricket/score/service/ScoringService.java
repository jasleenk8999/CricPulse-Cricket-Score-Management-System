package com.cricket.score.service;

import com.cricket.score.dto.BallEventRequest;
import com.cricket.score.model.*;
import com.cricket.score.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScoringService {

    private final MatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMatchStatRepository playerMatchStatRepository;
    private final BallEventRepository ballEventRepository;

    public ScoringService(MatchRepository matchRepository,
                          InningsRepository inningsRepository,
                          PlayerRepository playerRepository,
                          PlayerMatchStatRepository playerMatchStatRepository,
                          BallEventRepository ballEventRepository) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.playerRepository = playerRepository;
        this.playerMatchStatRepository = playerMatchStatRepository;
        this.ballEventRepository = ballEventRepository;
    }

    @Transactional
    public BallEvent recordBall(Long matchId, BallEventRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Match is already completed!");
        }

        Innings innings = inningsRepository.findByMatchIdAndInningsNumber(matchId, match.getCurrentInningsNumber())
                .orElseThrow(() -> new IllegalStateException("Active innings not found for match: " + matchId));

        if (match.getStatus() == MatchStatus.UPCOMING) {
            match.setStatus(MatchStatus.LIVE);
            matchRepository.save(match);
        }

        // Active players
        Player striker = (request.getStrikerId() != null) ?
                playerRepository.findById(request.getStrikerId()).orElse(innings.getCurrentStriker()) : innings.getCurrentStriker();
        Player nonStriker = (request.getNonStrikerId() != null) ?
                playerRepository.findById(request.getNonStrikerId()).orElse(innings.getCurrentNonStriker()) : innings.getCurrentNonStriker();
        Player bowler = (request.getBowlerId() != null) ?
                playerRepository.findById(request.getBowlerId()).orElse(innings.getCurrentBowler()) : innings.getCurrentBowler();

        if (striker == null || nonStriker == null || bowler == null) {
            initializeInningsPlayers(innings);
            striker = innings.getCurrentStriker();
            nonStriker = innings.getCurrentNonStriker();
            bowler = innings.getCurrentBowler();
        }

        // Get player stats
        PlayerMatchStat strikerStat = getOrCreatePlayerStat(match, innings, striker);
        PlayerMatchStat bowlerStat = getOrCreatePlayerStat(match, innings, bowler);

        if (strikerStat == null || bowlerStat == null) {
            throw new IllegalStateException("Striker or Bowler statistics could not be initialized. Check player roster.");
        }

        ExtraType extraType = request.getExtraType() != null ? request.getExtraType() : ExtraType.NONE;
        int runs = request.getRunsScored() != null ? request.getRunsScored() : 0;
        int extraRuns = request.getExtraRuns() != null ? request.getExtraRuns() : 0;
        boolean isWicket = Boolean.TRUE.equals(request.getIsWicket());

        boolean isLegalBall = (extraType != ExtraType.WIDE && extraType != ExtraType.NO_BALL);

        // Update Innings totals
        int totalBallRuns = runs;
        if (extraType == ExtraType.WIDE || extraType == ExtraType.NO_BALL) {
            totalBallRuns += Math.max(1, extraRuns);
        } else if (extraType == ExtraType.BYE || extraType == ExtraType.LEG_BYE) {
            totalBallRuns += extraRuns;
        }

        innings.setTotalRuns(innings.getTotalRuns() + totalBallRuns);

        if (extraType == ExtraType.WIDE) innings.setWides(innings.getWides() + 1 + extraRuns);
        if (extraType == ExtraType.NO_BALL) innings.setNoBalls(innings.getNoBalls() + 1 + extraRuns);
        if (extraType == ExtraType.BYE) innings.setByes(innings.getByes() + extraRuns);
        if (extraType == ExtraType.LEG_BYE) innings.setLegByes(innings.getLegByes() + extraRuns);

        // Update Striker Stat
        if (extraType != ExtraType.WIDE) {
            strikerStat.setBallsFaced(strikerStat.getBallsFaced() + 1);
        }
        if (extraType == ExtraType.NONE || extraType == ExtraType.NO_BALL) {
            strikerStat.setRunsScored(strikerStat.getRunsScored() + runs);
            if (runs == 4) strikerStat.setFours(strikerStat.getFours() + 1);
            if (runs == 6) strikerStat.setSixes(strikerStat.getSixes() + 1);
        }

        // Update Bowler Stat
        if (isLegalBall) {
            bowlerStat.setOversBowledLegalBalls(bowlerStat.getOversBowledLegalBalls() + 1);
        }
        if (extraType == ExtraType.WIDE) bowlerStat.setWideBallsBowled(bowlerStat.getWideBallsBowled() + 1);
        if (extraType == ExtraType.NO_BALL) bowlerStat.setNoBallsBowled(bowlerStat.getNoBallsBowled() + 1);
        
        // Bowler concedes runs (except byes / leg byes)
        if (extraType != ExtraType.BYE && extraType != ExtraType.LEG_BYE) {
            bowlerStat.setRunsConceded(bowlerStat.getRunsConceded() + totalBallRuns);
        }

        // Ball & Over counts
        if (isLegalBall) {
            int currentBalls = innings.getBallsInCurrentOver() + 1;
            if (currentBalls == 6) {
                innings.setCompletedOvers(innings.getCompletedOvers() + 1);
                innings.setBallsInCurrentOver(0);
            } else {
                innings.setBallsInCurrentOver(currentBalls);
            }
        }

        // Handle Wicket
        Player dismissedPlayer = null;
        if (isWicket) {
            innings.setTotalWickets(innings.getTotalWickets() + 1);
            if (request.getDismissalType() != DismissalType.RUN_OUT) {
                bowlerStat.setWicketsTaken(bowlerStat.getWicketsTaken() + 1);
            }

            dismissedPlayer = (request.getDismissedPlayerId() != null) ?
                    playerRepository.findById(request.getDismissedPlayerId()).orElse(striker) : striker;

            PlayerMatchStat dismissedStat = getOrCreatePlayerStat(match, innings, dismissedPlayer);
            dismissedStat.setIsOut(true);
            dismissedStat.setDismissalInfo(formatDismissal(request.getDismissalType(), bowler, request.getCommentary()));
            playerMatchStatRepository.save(dismissedStat);

            // Bring in next batsman if available
            Player nextBatsman = findNextAvailableBatsman(innings);
            if (dismissedPlayer.getId().equals(striker.getId())) {
                striker = nextBatsman;
            } else {
                nonStriker = nextBatsman;
            }
        }

        // Handle Strike Rotation
        boolean overEnded = (isLegalBall && innings.getBallsInCurrentOver() == 0);
        boolean shouldRotateStrike = (runs % 2 != 0);

        if (shouldRotateStrike && !isWicket) {
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;
        }

        if (overEnded) {
            // End of over: rotate strike & pick next bowler
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;

            Player nextBowler = selectNextBowler(innings, bowler);
            bowler = nextBowler;
        }

        innings.setCurrentStriker(striker);
        innings.setCurrentNonStriker(nonStriker);
        innings.setCurrentBowler(bowler);

        playerMatchStatRepository.save(strikerStat);
        playerMatchStatRepository.save(bowlerStat);

        // Save Ball Event
        BallEvent ballEvent = new BallEvent();
        ballEvent.setMatch(match);
        ballEvent.setInnings(innings);
        ballEvent.setOverNumber(innings.getCompletedOvers());
        ballEvent.setBallNumber(innings.getBallsInCurrentOver());
        ballEvent.setStriker(strikerStat.getPlayer());
        ballEvent.setNonStriker(nonStriker != null ? nonStriker : strikerStat.getPlayer());
        ballEvent.setBowler(bowlerStat.getPlayer());
        ballEvent.setRunsScored(runs);
        ballEvent.setExtraType(extraType);
        ballEvent.setExtraRuns(extraRuns);
        ballEvent.setIsWicket(isWicket);
        ballEvent.setDismissalType(request.getDismissalType());
        ballEvent.setDismissedPlayer(dismissedPlayer);

        String comm = request.getCommentary();
        if (comm == null || comm.isBlank()) {
            comm = generateCommentary(bowlerStat.getPlayer().getName(), strikerStat.getPlayer().getName(), runs, extraType, isWicket);
        }
        ballEvent.setCommentary(comm);
        ballEvent.setTimestamp(LocalDateTime.now());

        BallEvent savedBall = ballEventRepository.save(ballEvent);

        // Check Innings & Match Completion
        checkInningsAndMatchState(match, innings);

        inningsRepository.save(innings);
        matchRepository.save(match);

        return savedBall;
    }

    public void initializeInningsPlayers(Innings innings) {
        List<Player> batPlayers = playerRepository.findByTeamId(innings.getBattingTeam().getId());
        List<Player> bowlPlayers = playerRepository.findByTeamId(innings.getBowlingTeam().getId());

        if (!batPlayers.isEmpty()) innings.setCurrentStriker(batPlayers.get(0));
        if (batPlayers.size() > 1) innings.setCurrentNonStriker(batPlayers.get(1));

        if (!bowlPlayers.isEmpty()) {
            // Find a bowler or all-rounder
            Player firstBowler = bowlPlayers.stream()
                    .filter(p -> p.getRole() == Role.BOWLER || p.getRole() == Role.ALL_ROUNDER)
                    .findFirst().orElse(bowlPlayers.get(bowlPlayers.size() - 1));
            innings.setCurrentBowler(firstBowler);
        }
    }

    private PlayerMatchStat getOrCreatePlayerStat(Match match, Innings innings, Player player) {
        if (player == null) return null;
        return playerMatchStatRepository.findByInningsIdAndPlayerId(innings.getId(), player.getId())
                .orElseGet(() -> {
                    PlayerMatchStat stat = new PlayerMatchStat();
                    stat.setMatch(match);
                    stat.setInnings(innings);
                    stat.setPlayer(player);
                    stat.setIsOut(false);
                    stat.setDismissalInfo("not out");
                    return playerMatchStatRepository.save(stat);
                });
    }

    private Player findNextAvailableBatsman(Innings innings) {
        List<Player> teamPlayers = playerRepository.findByTeamId(innings.getBattingTeam().getId());
        List<PlayerMatchStat> stats = playerMatchStatRepository.findByInningsId(innings.getId());

        for (Player p : teamPlayers) {
            boolean alreadyBatted = stats.stream().anyMatch(s -> s.getPlayer().getId().equals(p.getId()));
            if (!alreadyBatted) {
                return p;
            }
        }
        return null; // All out or no more players
    }

    private Player selectNextBowler(Innings innings, Player currentBowler) {
        List<Player> bowlPlayers = playerRepository.findByTeamId(innings.getBowlingTeam().getId());
        List<Player> bowlersOnly = bowlPlayers.stream()
                .filter(p -> p.getRole() == Role.BOWLER || p.getRole() == Role.ALL_ROUNDER)
                .toList();

        if (bowlersOnly.isEmpty()) bowlersOnly = bowlPlayers;

        for (Player b : bowlersOnly) {
            if (!b.getId().equals(currentBowler.getId())) {
                return b;
            }
        }
        return currentBowler;
    }

    private void checkInningsAndMatchState(Match match, Innings innings) {
        boolean maxOversReached = innings.getCompletedOvers() >= match.getTotalOvers();
        boolean allOut = innings.getTotalWickets() >= 10 || innings.getCurrentStriker() == null;
        boolean targetPassed = (innings.getInningsNumber() == 2 && innings.getTargetRuns() != null && innings.getTotalRuns() >= innings.getTargetRuns());

        if (innings.getInningsNumber() == 1) {
            if (maxOversReached || allOut) {
                innings.setIsCompleted(true);
                match.setStatus(MatchStatus.INNINGS_BREAK);
                match.setCurrentInningsNumber(2);

                // Create Second Innings
                Innings secondInnings = new Innings();
                secondInnings.setMatch(match);
                secondInnings.setInningsNumber(2);
                secondInnings.setBattingTeam(match.getTossDecision() == TossDecision.BAT ? match.getTeamB() : match.getTeamA());
                secondInnings.setBowlingTeam(match.getTossDecision() == TossDecision.BAT ? match.getTeamA() : match.getTeamB());
                secondInnings.setTargetRuns(innings.getTotalRuns() + 1);
                initializeInningsPlayers(secondInnings);
                inningsRepository.save(secondInnings);
            }
        } else if (innings.getInningsNumber() == 2) {
            if (targetPassed) {
                innings.setIsCompleted(true);
                match.setStatus(MatchStatus.COMPLETED);
                match.setWinnerTeam(innings.getBattingTeam());
                int wicketsLeft = 10 - innings.getTotalWickets();
                match.setResultSummary(innings.getBattingTeam().getName() + " won by " + wicketsLeft + " wickets!");
            } else if (maxOversReached || allOut) {
                innings.setIsCompleted(true);
                match.setStatus(MatchStatus.COMPLETED);
                if (innings.getTotalRuns() < innings.getTargetRuns() - 1) {
                    int margin = (innings.getTargetRuns() - 1) - innings.getTotalRuns();
                    match.setWinnerTeam(innings.getBowlingTeam());
                    match.setResultSummary(innings.getBowlingTeam().getName() + " won by " + margin + " runs!");
                } else {
                    match.setResultSummary("Match Tied!");
                }
            }
        }
    }

    private String formatDismissal(DismissalType type, Player bowler, String customText) {
        if (type == DismissalType.BOWLED) return "b " + bowler.getName();
        if (type == DismissalType.CAUGHT) return "c Fielder b " + bowler.getName();
        if (type == DismissalType.LBW) return "lbw b " + bowler.getName();
        if (type == DismissalType.RUN_OUT) return "run out";
        if (type == DismissalType.STUMPED) return "st Keeper b " + bowler.getName();
        return "out";
    }

    private String generateCommentary(String bowlerName, String strikerName, int runs, ExtraType extra, boolean isWicket) {
        if (isWicket) return bowlerName + " to " + strikerName + ", OUT! What a delivery, clean dismissal!";
        if (extra == ExtraType.WIDE) return bowlerName + " to " + strikerName + ", WIDE ball down the leg side.";
        if (extra == ExtraType.NO_BALL) return bowlerName + " to " + strikerName + ", NO BALL! Free hit coming up!";
        if (runs == 6) return bowlerName + " to " + strikerName + ", SIX! Massive hit over deep mid-wicket!";
        if (runs == 4) return bowlerName + " to " + strikerName + ", FOUR! Crack into the gap to the boundary!";
        if (runs == 0) return bowlerName + " to " + strikerName + ", No run, solid defensive shot.";
        return bowlerName + " to " + strikerName + ", " + runs + " run(s) taken.";
    }
}
