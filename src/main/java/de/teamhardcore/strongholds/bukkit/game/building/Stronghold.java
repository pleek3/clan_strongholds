package de.teamhardcore.strongholds.bukkit.game.building;

import de.teamhardcore.strongholds.bukkit.configs.BuildingConfig;
import de.teamhardcore.strongholds.bukkit.game.phases.CollectingPhase;
import de.teamhardcore.strongholds.bukkit.game.phases.Phase;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Stronghold {

    private final BuildingConfig config;
    private final String name;
    private final Location maxPos;
    private final Location minPos;
    private Phase phase;

    public Stronghold(final BuildingConfig config) {
        this.config = config;
        this.maxPos = config.getMaxPos();
        this.minPos = config.getMinPos();
        this.name = config.getName();

        if (this.maxPos != null && this.minPos != null)
            changePhase(new CollectingPhase(this));
    }

    public void changePhase(final Phase phase) {
        if (this.phase != null)
            this.phase.onEnd();

        this.phase = phase;
        this.phase.onStart();

        System.out.println("started phase: " + phase.getName());
    }

    public List<Player> getPlayersInStronghold() {
        return this.minPos.getWorld()
                .getPlayers()
                .stream()
                .filter(player -> LocationUtil.isInside(minPos, maxPos, player.getLocation()))
                .collect(Collectors.toList());
    }

}
