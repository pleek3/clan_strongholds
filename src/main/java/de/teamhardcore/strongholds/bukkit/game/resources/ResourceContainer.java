package de.teamhardcore.strongholds.bukkit.game.resources;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResourceContainer {

    private final Map<Material, Integer> collectedResources = new HashMap<>();

    public void add(final Material material, final int amount) {
        this.collectedResources.compute(material, (material1, integer) -> (integer == null) ? 1 : integer + amount);
    }

}
