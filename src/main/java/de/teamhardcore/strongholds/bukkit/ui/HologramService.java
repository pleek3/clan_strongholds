package de.teamhardcore.strongholds.bukkit.ui;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface HologramService {

    void createDefaultHologram(final String id, final Location location, final List<String> lines, List<Player> players);

    void removeHologram(final String id);

    void updateLine(final String id, final int index, final String line);

}
