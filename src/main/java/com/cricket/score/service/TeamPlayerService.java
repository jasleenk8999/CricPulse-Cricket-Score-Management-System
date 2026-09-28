package com.cricket.score.service;

import com.cricket.score.model.Player;
import com.cricket.score.model.Team;
import com.cricket.score.repository.PlayerRepository;
import com.cricket.score.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamPlayerService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamPlayerService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + id));
    }

    @Transactional
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Player> getPlayersByTeamId(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + id));
    }

    @Transactional
    public Player addPlayer(Player player, Long teamId) {
        Team team = getTeamById(teamId);
        player.setTeam(team);
        return playerRepository.save(player);
    }
}
