package de.teamhardcore.strongholds.bukkit.game.phases;

import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import de.teamhardcore.strongholds.bukkit.game.building.Stronghold;
import de.teamhardcore.strongholds.bukkit.team.Team;
import de.teamhardcore.strongholds.bukkit.ui.HologramService;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import de.teamhardcore.strongholds.bukkit.utils.ParticleEffect;
import de.teamhardcore.strongholds.bukkit.utils.PlayerUtil;
import de.teamhardcore.strongholds.bukkit.utils.SkinUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AttackPhase implements Phase {

    private static final int MAX_CURRENT_BUILDING_HEALTH = 200;

    private final Stronghold building;
    private final Team capturedTeam;
    private final HologramService hologramService;

    private long currentBuildingHealth;
    private boolean canAttack;

    public AttackPhase(final Stronghold building, final Team capturedTeam) {
        this.building = building;
        this.capturedTeam = capturedTeam;
        this.currentBuildingHealth = MAX_CURRENT_BUILDING_HEALTH;
        this.canAttack = true;

        this.hologramService = MineQuestStrongholdPlugin.INSTANCE().getHologramService();
    }

    @Override
    public void onStart() {
        List<String> hologramLines = new ArrayList<>();

        hologramLines.add("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
        hologramLines.add(
                "§eDer Clan-Stützpunkt wird vom Team §a§l" + getCapturedTeam().getName() + " §ekontrolliert.");
        hologramLines.add(" ");
        hologramLines.add("§eAktuelle Gesundheit des Clan-Stützpunktes: §7" + this.currentBuildingHealth + " §c❤");


        Location center = LocationUtil.getCenterLocation(this.building.getMinPos(), this.building.getMaxPos());
        this.hologramService.createDefaultHologram(HOLOGRAM_PHASE_ID, center, hologramLines,
                new ArrayList<>(Bukkit.getOnlinePlayers()));

        SkinUtil.paste(center, this.building.getConfig().getAttackPhaseSchematicFileName(),
                this.building.getConfig().getRotation());

        LocationUtil.generateRandomLocations(center, 10, 20).forEach(location -> {
            ParticleEffect.SMOKE_LARGE.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.SMOKE_NORMAL.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.SMOKE_LARGE.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.CLOUD.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.CLOUD.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.CLOUD.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
        });
    }

    @Override
    public void onEnd() {

    }

    @Override
    public String getName() {
        return "Verteidigen";
    }

    public void attackBuilding(final Player player) {
        if (!this.canAttack) {
            return;
        }

       /* if (this.capturedTeam.getMembers().contains(player)) {
            return;
        }*/

        if (this.currentBuildingHealth <= 0) {
            this.canAttack = false;
            this.breakBuilding();
            return;
        }

        long damage = this.calculateDamageByHandItem(player.getItemInHand());
        this.currentBuildingHealth -= damage;

        if (this.currentBuildingHealth - damage < 0)
            this.currentBuildingHealth = 0;


        this.hologramService.updateLine(HOLOGRAM_PHASE_ID, 3,
                "§eAktuelle Gesundheit des Clan-Stützpunktes: §7" + this.currentBuildingHealth + " §c❤");

        PlayerUtil.sendActionText(player,
                "§a§l- " + damage + " §c❤! §8(§a" + this.currentBuildingHealth + "§c❤§8/§7" + MAX_CURRENT_BUILDING_HEALTH + "§c❤§8)");
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 0.70F, 0.3F);

    }

    public void breakBuilding() {
        this.building.changePhase(new CollectingPhase(getBuilding()));

        Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
        Bukkit.broadcastMessage(
                "§eDer §7%BASE% Stützpunkt §ekonnte zurückerobert werden.!".replace("%BASE%", this.building.getName()));
        Bukkit.broadcastMessage(
                "§eVersuche jetzt den Stützpunkt einzunehmen. Heroische Belohnungen und Vorteile warten auf dich und dein Team.");
        Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");


        Location originLocation = LocationUtil.getCenterLocation(this.building.getMinPos(), this.building.getMaxPos());

        LocationUtil.generateRandomLocations(originLocation, 10, 20).forEach(location -> {
            ParticleEffect.EXPLOSION_LARGE.display(4.5F, 4.05F, 4.05F, 0.0F, 15, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.SMOKE_LARGE.display(4.5F, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D), 30.0D);
            ParticleEffect.EXPLOSION_NORMAL.display(4.5f, 4.05F, 4.05F, 0.0F, 50, location.add(0.5D, 0.5D, 0.5D),
                    30.0D);
        });
    }

    private long calculateDamageByHandItem(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return 1;

        Material type = itemStack.getType();

        if (type.name().endsWith("_SWORD")) {
            return 10;
        }

        return 1;
    }
}
