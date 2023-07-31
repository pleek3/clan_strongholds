package de.teamhardcore.strongholds.bukkit.team;

import org.bukkit.entity.Player;

import java.util.List;

//can also be your clan class
public interface Team {

    String getName();

    List<Player> getMembers();
}
