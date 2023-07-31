package de.teamhardcore.strongholds.bukkit.team.service;

import de.teamhardcore.strongholds.bukkit.team.Team;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MineQuestTeamService implements TeamService {
    
    private Team team;

    @Override
    public Team getTeamForPlayer(Player player) {
        if (team == null) {
            team = new Team() {
                @Override
                public String getName() {
                    return "Teamhardcores Team";
                }

                @Override
                public List<Player> getMembers() {
                    return Collections.singletonList(player);
                }
            };
        }

        return this.team;
    }

    @Override
    public void addPlayerToTeam(Player player, Team team) {
    }
}
