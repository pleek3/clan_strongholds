package de.teamhardcore.strongholds.bukkit.configs;

import de.teamhardcore.strongholds.bukkit.game.resources.ResourceContainer;
import de.teamhardcore.strongholds.bukkit.team.Team;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BuildingConfig extends FileBase {

    private final List<Material> collectableMaterials = new ArrayList<>();

    //todo: json array with json object as entries
    private final Map<Material, Integer> amountOfResourcesToCollect = new HashMap<>(); //todo: rename name?

    private final String name;
    private Location maxPos;
    private Location minPos;

    private String collectingPhaseSchematicFileName;
    private String attackPhaseSchematicFileName;

    private int rotation;

    public BuildingConfig(final String name) {
        super("strongholds", name);
        this.maxPos = null;
        this.minPos = null;
        this.name = name;
        init();
    }

    @Override
    public void init() {
        writeDefaults();
        loadPositions();
    }

    private void loadPositions() {
        FileConfiguration config = getConfig();

        if (config.get("maxPos") != null) {
            this.maxPos = LocationUtil.convertStringToLocation(config.getString("maxPos"));
        }

        if (config.get("minPos") != null) {
            this.maxPos = LocationUtil.convertStringToLocation(config.getString("minPos"));
        }
    }

    public void setMaxPos(Location maxPos) {
        this.maxPos = maxPos;
        getConfig().set("maxPos", LocationUtil.convertLocationToString(maxPos));
        saveConfig();
    }

    public void setMinPos(Location minPos) {
        this.minPos = minPos;
        getConfig().set("minPos", LocationUtil.convertLocationToString(minPos));
        saveConfig();
    }

    @Override
    public void writeDefaults() {
        this.collectableMaterials.add(Material.STONE);
        this.amountOfResourcesToCollect.put(Material.STONE, 10);
        this.rotation = 0;
        this.collectingPhaseSchematicFileName = "b1";
        this.attackPhaseSchematicFileName = "b2";
    }

    public boolean isCollectable(final Material material) {
        return this.collectableMaterials.contains(material);
    }

    public boolean hasAllCollected(final Team team, final ResourceContainer container) {
        for (Map.Entry<Material, Integer> entry : container.getCollectedResources().entrySet()) {
            if (!this.amountOfResourcesToCollect.containsKey(entry.getKey())) return false;
            if (amountOfResourcesToCollect.get(entry.getKey()) > entry.getValue()) return false;
        }

        return true;
    }

}
