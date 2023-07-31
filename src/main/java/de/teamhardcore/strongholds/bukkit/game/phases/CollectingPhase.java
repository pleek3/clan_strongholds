package de.teamhardcore.strongholds.bukkit.game.phases;

import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import de.teamhardcore.strongholds.bukkit.game.building.Stronghold;
import de.teamhardcore.strongholds.bukkit.game.resources.ResourceContainer;
import de.teamhardcore.strongholds.bukkit.team.Team;
import de.teamhardcore.strongholds.bukkit.ui.HologramService;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import de.teamhardcore.strongholds.bukkit.utils.PlayerUtil;
import de.teamhardcore.strongholds.bukkit.utils.SkinUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class CollectingPhase implements Phase {

    private final HologramService hologramService;
    private final Stronghold building;
    private final Map<Team, ResourceContainer> teamResources = new HashMap<>();

    public CollectingPhase(final Stronghold building) {
        this.building = building;
        this.hologramService = MineQuestStrongholdPlugin.INSTANCE().getHologramService();
    }

    @Override
    public void onStart() {
        List<String> hologramLines = new ArrayList<>();

        hologramLines.add("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
        hologramLines.add("§a§l" + this.building.getName());
        hologramLines.add(" ");
        hologramLines.add("§7Sammel folgende Ressourcen: ");

        for (Material material : this.building.getConfig().getCollectableMaterials()) {
            int amountToCollect = this.building.getConfig().getAmountOfResourcesToCollect().getOrDefault(material, 0);
            String line = "§8- §7" + amountToCollect + "x §c" + material.name();
            hologramLines.add(line);
        }

        Location center = LocationUtil.getCenterLocation(this.building.getMinPos(), this.building.getMaxPos());
        this.hologramService.createDefaultHologram(HOLOGRAM_PHASE_ID, center, hologramLines,
                new ArrayList<>(Bukkit.getOnlinePlayers()));

        SkinUtil.paste(center, this.building.getConfig().getCollectingPhaseSchematicFileName(),
                this.building.getConfig().getRotation());
    }

    @Override
    public void onEnd() {
        this.hologramService.removeHologram(HOLOGRAM_PHASE_ID);
    }

    @Override
    public String getName() {
        return "Ressourcen sammeln";
    }

    public void collectResourceForTeam(final Player player, final Team team, final Material material, final int amount) {
        if (!this.building.getConfig().isCollectable(material)) return;

        ResourceContainer container = this.teamResources.get(team);

        if (container == null) {
            container = new ResourceContainer();
        }

        container.add(material, amount);
        this.teamResources.put(team, container);

        if (this.building.getConfig().hasAllCollected(team, container)) {
            team.getMembers().stream().filter(Objects::nonNull).forEach(member -> {
                PlayerUtil.sendActionText(member, "§a§lGLÜCKWUNSCH! §fDein Team hat alle Ressourcen zusammengebracht.");
            });

            Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
            Bukkit.broadcastMessage(
                    "§eDas Team §a§l" + team.getName() + " §ehat den Stützpunkt §7" + getBuilding().getName() + " §eeingenommen!");
            Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");

            //todo: change skin with fawe
            this.building.changePhase(new CapturePhase(getBuilding(), team));
        }
    }

}
