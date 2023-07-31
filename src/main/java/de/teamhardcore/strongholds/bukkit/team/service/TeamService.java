package de.teamhardcore.strongholds.bukkit.team.service;

import de.teamhardcore.strongholds.bukkit.team.Team;
import org.bukkit.entity.Player;

public interface TeamService {

    Team getTeamForPlayer(final Player player);

    void addPlayerToTeam(final Player player, final Team team);

}
