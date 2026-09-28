package com.cricket.score.config;

import com.cricket.score.dto.BallEventRequest;
import com.cricket.score.dto.MatchCreateRequest;
import com.cricket.score.model.*;
import com.cricket.score.repository.MatchRepository;
import com.cricket.score.repository.PlayerRepository;
import com.cricket.score.repository.TeamRepository;
import com.cricket.score.service.MatchService;
import com.cricket.score.service.ScoringService;
import com.cricket.score.service.SimulationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final ScoringService scoringService;
    private final SimulationService simulationService;

    public DataInitializer(TeamRepository teamRepository,
                           PlayerRepository playerRepository,
                           MatchRepository matchRepository,
                           MatchService matchService,
                           ScoringService scoringService,
                           SimulationService simulationService) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.scoringService = scoringService;
        this.simulationService = simulationService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (teamRepository.count() > 0) return;

        System.out.println(">>> Initializing Live Cricket Platform Sample Data...");

        // 1. Create Teams
        Team ind = teamRepository.save(new Team("India", "IND", "India", "#1E88E5"));
        Team aus = teamRepository.save(new Team("Australia", "AUS", "Australia", "#FDD835"));
        Team csk = teamRepository.save(new Team("Chennai Super Kings", "CSK", "India", "#FFB300"));
        Team mi = teamRepository.save(new Team("Mumbai Indians", "MI", "India", "#00838F"));
        Team eng = teamRepository.save(new Team("England", "ENG", "England", "#E53935"));

        // 2. Add India Players
        Player rohit = playerRepository.save(new Player("Rohit Sharma", Role.BATSMAN, "Right-hand", "Right-arm Offbreak", 45, ind));
        Player kohli = playerRepository.save(new Player("Virat Kohli", Role.BATSMAN, "Right-hand", "Right-arm Medium", 18, ind));
        Player surya = playerRepository.save(new Player("Suryakumar Yadav", Role.BATSMAN, "Right-hand", "Right-arm Offbreak", 63, ind));
        Player pant = playerRepository.save(new Player("Rishabh Pant", Role.WICKET_KEEPER, "Left-hand", "None", 17, ind));
        Player hardik = playerRepository.save(new Player("Hardik Pandya", Role.ALL_ROUNDER, "Right-hand", "Right-arm Fast-Medium", 33, ind));
        Player jadeja = playerRepository.save(new Player("Ravindra Jadeja", Role.ALL_ROUNDER, "Left-hand", "Slow Left-arm Orthodox", 8, ind));
        Player bumrah = playerRepository.save(new Player("Jasprit Bumrah", Role.BOWLER, "Right-hand", "Right-arm Fast", 93, ind));
        Player shami = playerRepository.save(new Player("Mohammed Shami", Role.BOWLER, "Right-hand", "Right-arm Fast", 11, ind));
        Player kuldeep = playerRepository.save(new Player("Kuldeep Yadav", Role.BOWLER, "Left-hand", "Left-arm Unorthodox", 23, ind));
        Player arshdeep = playerRepository.save(new Player("Arshdeep Singh", Role.BOWLER, "Left-hand", "Left-arm Medium-Fast", 2, ind));
        Player jaiswal = playerRepository.save(new Player("Yashasvi Jaiswal", Role.BATSMAN, "Left-hand", "Right-arm Legbreak", 64, ind));

        // 3. Add Australia Players
        Player head = playerRepository.save(new Player("Travis Head", Role.BATSMAN, "Left-hand", "Right-arm Offbreak", 62, aus));
        Player warner = playerRepository.save(new Player("David Warner", Role.BATSMAN, "Left-hand", "Right-arm Legbreak", 31, aus));
        Player smith = playerRepository.save(new Player("Steve Smith", Role.BATSMAN, "Right-hand", "Right-arm Legbreak", 49, aus));
        Player maxwell = playerRepository.save(new Player("Glenn Maxwell", Role.ALL_ROUNDER, "Right-hand", "Right-arm Offbreak", 32, aus));
        Player stoinis = playerRepository.save(new Player("Marcus Stoinis", Role.ALL_ROUNDER, "Right-hand", "Right-arm Medium-Fast", 17, aus));
        Player carey = playerRepository.save(new Player("Alex Carey", Role.WICKET_KEEPER, "Left-hand", "None", 4, aus));
        Player cummins = playerRepository.save(new Player("Pat Cummins", Role.BOWLER, "Right-hand", "Right-arm Fast", 30, aus));
        Player starc = playerRepository.save(new Player("Mitchell Starc", Role.BOWLER, "Left-hand", "Left-arm Fast", 56, aus));
        Player zampa = playerRepository.save(new Player("Adam Zampa", Role.BOWLER, "Right-hand", "Right-arm Legbreak", 88, aus));
        Player hazlewood = playerRepository.save(new Player("Josh Hazlewood", Role.BOWLER, "Left-hand", "Right-arm Fast-Medium", 38, aus));
        Player green = playerRepository.save(new Player("Cameron Green", Role.ALL_ROUNDER, "Right-hand", "Right-arm Fast-Medium", 42, aus));

        // 4. Add CSK Players
        playerRepository.save(new Player("MS Dhoni", Role.WICKET_KEEPER, "Right-hand", "Right-arm Medium", 7, csk));
        playerRepository.save(new Player("Ruturaj Gaikwad", Role.BATSMAN, "Right-hand", "Right-arm Offbreak", 31, csk));
        playerRepository.save(new Player("Shivam Dube", Role.ALL_ROUNDER, "Left-hand", "Right-arm Medium", 25, csk));
        playerRepository.save(new Player("Deepak Chahar", Role.BOWLER, "Right-hand", "Right-arm Medium-Fast", 90, csk));

        // 5. Add MI Players
        playerRepository.save(new Player("Ishan Kishan", Role.WICKET_KEEPER, "Left-hand", "None", 23, mi));
        playerRepository.save(new Player("Tilak Varma", Role.BATSMAN, "Left-hand", "Right-arm Offbreak", 9, mi));
        playerRepository.save(new Player("Piyush Chawla", Role.BOWLER, "Left-hand", "Right-arm Legbreak", 11, mi));

        // 6. Add England Players
        Player buttler = playerRepository.save(new Player("Jos Buttler", Role.WICKET_KEEPER, "Right-hand", "None", 63, eng));
        Player brook = playerRepository.save(new Player("Harry Brook", Role.BATSMAN, "Right-hand", "Right-arm Medium", 88, eng));
        Player stokes = playerRepository.save(new Player("Ben Stokes", Role.ALL_ROUNDER, "Left-hand", "Right-arm Fast-Medium", 55, eng));
        Player curran = playerRepository.save(new Player("Sam Curran", Role.ALL_ROUNDER, "Left-hand", "Left-arm Medium-Fast", 58, eng));
        Player wood = playerRepository.save(new Player("Mark Wood", Role.BOWLER, "Right-hand", "Right-arm Fast", 33, eng));
        Player rashid = playerRepository.save(new Player("Adil Rashid", Role.BOWLER, "Right-hand", "Right-arm Legbreak", 95, eng));

        // 6. Create Ongoing Live Match 1: India vs Australia
        MatchCreateRequest m1 = new MatchCreateRequest();
        m1.setTitle("1st T20I: India vs Australia");
        m1.setMatchFormat(MatchFormat.T20);
        m1.setVenue("Wankhede Stadium, Mumbai");
        m1.setTotalOvers(20);
        m1.setTeamAId(ind.getId());
        m1.setTeamBId(aus.getId());
        m1.setTossWinnerId(ind.getId());
        m1.setTossDecision(TossDecision.BAT);

        Match match1 = matchService.createMatch(m1);

        // Simulate 24 balls of live action for India's 1st innings to make the live score rich!
        for (int i = 0; i < 24; i++) {
            simulationService.simulateBall(match1.getId());
        }

        // 7. Create Upcoming Match 2: CSK vs MI
        MatchCreateRequest m2 = new MatchCreateRequest();
        m2.setTitle("IPL Match 15: CSK vs MI");
        m2.setMatchFormat(MatchFormat.T20);
        m2.setVenue("M. A. Chidambaram Stadium, Chennai");
        m2.setTotalOvers(20);
        m2.setTeamAId(csk.getId());
        m2.setTeamBId(mi.getId());
        m2.setTossWinnerId(csk.getId());
        m2.setTossDecision(TossDecision.BAT);

        Match match2 = matchService.createMatch(m2);
        match2.setStatus(MatchStatus.UPCOMING);
        matchRepository.save(match2);

        // 8. Create Completed Match 3: India vs England (Fully Simulated)
        MatchCreateRequest m3 = new MatchCreateRequest();
        m3.setTitle("Warm-Up Match: India vs England");
        m3.setMatchFormat(MatchFormat.T20);
        m3.setVenue("Eden Gardens, Kolkata");
        m3.setTotalOvers(5); // 5 over exhibition match
        m3.setTeamAId(ind.getId());
        m3.setTeamBId(eng.getId());
        m3.setTossWinnerId(eng.getId());
        m3.setTossDecision(TossDecision.BAT);

        Match match3 = matchService.createMatch(m3);
        simulationService.simulateMatch(match3.getId());

        System.out.println(">>> Live Cricket Platform Sample Data Loaded Successfully!");
    }
}
