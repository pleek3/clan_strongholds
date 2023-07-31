package de.teamhardcore.strongholds.bukkit.game.service;

import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import de.teamhardcore.strongholds.bukkit.game.building.Stronghold;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Getter
public class StrongholdGameService {

    public final static String STRONGHOLD_DIRECTORY = MineQuestStrongholdPlugin.INSTANCE().getDataFolder() + File.separator + "strongholds";

    private final Set<Stronghold> strongholds = new HashSet<>();
    private final StrongholdBuildingConfigService strongholdBuildingConfigService;

    public StrongholdGameService(final StrongholdBuildingConfigService strongholdBuildingConfigService) {
        this.strongholdBuildingConfigService = strongholdBuildingConfigService;
        initStrongholds();
    }

    private void initStrongholds() {
        this.strongholdBuildingConfigService.loadBuildingConfigs(STRONGHOLD_DIRECTORY)
                .forEach(config -> this.strongholds.add(new Stronghold(config)));
    }

    public void addStronghold(final Stronghold stronghold) {
        //todo: add skin with fawe
        this.strongholds.add(stronghold);
    }

    public void removeStronghold(final Stronghold stronghold) {
        //todo: remove skin with fawe?
        this.strongholds.remove(stronghold);
    }

    public Stronghold findStrongholdByName(final String name) {
        return this.strongholds.stream()
                .filter(stronghold -> stronghold.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Stronghold findStrongholdByLocation(final Location location) {
        return this.strongholds.stream()
                .filter(stronghold -> stronghold.getMaxPos() != null)
                .filter(stronghold -> stronghold.getMinPos() != null)
                .filter(stronghold -> LocationUtil.isInside(stronghold.getMinPos(), stronghold.getMaxPos(), location))
                .findFirst()
                .orElse(null);
    }

}
