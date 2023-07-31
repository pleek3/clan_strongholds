package de.teamhardcore.strongholds.bukkit.game.phases;

import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import de.teamhardcore.strongholds.bukkit.game.building.Stronghold;
import de.teamhardcore.strongholds.bukkit.team.Team;
import de.teamhardcore.strongholds.bukkit.ui.HologramService;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import de.teamhardcore.strongholds.bukkit.utils.PlayerRewardUtil;
import de.teamhardcore.strongholds.bukkit.utils.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CapturePhase implements Phase {

    private final static int SECONDS_UNTIL_CANCEL_CAPTURE = MineQuestStrongholdPlugin.INSTANCE().getConfigurationContainer().getStrongholdConfig().getCancelCaptureTime();
    private final static int SECONDS_UNTIL_COMPLETE_CAPTURE = MineQuestStrongholdPlugin.INSTANCE().getConfigurationContainer().getStrongholdConfig().getTakingTime();

    private final HologramService hologramService;
    private final Stronghold building;
    private final BukkitTask checkForPlayersTask;
    private final Team capturingTeam;
    private int secondsWithNoPlayersInStronghold = 0;
    private int secondsUntilCompleteCapture = SECONDS_UNTIL_COMPLETE_CAPTURE;

    public CapturePhase(final Stronghold building, final Team capturingTeam) {
        this.building = building;
        this.capturingTeam = capturingTeam;
        this.checkForPlayersTask = Bukkit.getScheduler().runTaskTimer(MineQuestStrongholdPlugin.INSTANCE(), this::tick,
                20L, 20L);

        this.hologramService = MineQuestStrongholdPlugin.INSTANCE().getHologramService();
    }

    private void tick() {
        if (getTeamMembersInStronghold().size() <= 0) {
            this.secondsUntilCompleteCapture = SECONDS_UNTIL_COMPLETE_CAPTURE;

            if (++this.secondsWithNoPlayersInStronghold >= SECONDS_UNTIL_CANCEL_CAPTURE) {
                Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
                Bukkit.broadcastMessage(
                        "§eEs ist eine epische Schlacht entbrannt, doch das tapfere Team %TEAM% §ekonnte den Clan-Stützpunkt nicht erobern.".replace(
                                "%TEAM%", "§6§l" + this.capturingTeam.getName()));
                Bukkit.broadcastMessage("§eDer Stützpunkt kann jetzt wieder eingenommen werden.");
                Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");

                getCapturingTeam().getMembers().forEach(player -> PlayerUtil.sendActionText(player,
                        "§cDein Team konnte den Stützpunkt nicht verteidigen."));

                this.stopTaskAndChangePhase(new CollectingPhase(getBuilding()));
            }

            getPlayersNearbyCenterLocation().stream().filter(Objects::nonNull).forEach(player -> {
                int secondsUntilCanceledCapture = SECONDS_UNTIL_CANCEL_CAPTURE - this.secondsWithNoPlayersInStronghold;
                PlayerUtil.sendActionText(player,
                        "§4§lACHTUNG! §cDer Stützpunkt ist in §7" + secondsUntilCanceledCapture + " Sekunden §czurückerobert.");
            });
            return;
        }

        this.secondsWithNoPlayersInStronghold = 0;
        this.secondsUntilCompleteCapture--;

        getCapturingTeam().getMembers().stream().filter(Objects::nonNull).forEach(player -> {
            PlayerUtil.sendActionText(player,
                    "§fDer Stützpunkt ist in §a" + this.secondsUntilCompleteCapture + " Sekunden §feingenommen.");
        });

        this.hologramService.updateLine(HOLOGRAM_PHASE_ID, 5,
                "§eZeit bis zur Einnahme: §c§l" + this.secondsUntilCompleteCapture + " Sekunden");

        if (this.secondsUntilCompleteCapture <= 0) {
            this.stopTaskAndChangePhase(new AttackPhase(getBuilding(), this.capturingTeam));

            Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
            Bukkit.broadcastMessage(
                    "§eDas Team %TEAM% §ekonnte die epische Schlacht um den §7%BASE% Stützpunkt §efür sich gewinnen!".replace(
                            "%TEAM%", "§6§l" + this.capturingTeam.getName()).replace("%BASE%",
                            this.building.getName()));
            Bukkit.broadcastMessage("§eHeroische Belohnungen und Vorteile warten darauf abgeholt zu werden.");
            Bukkit.broadcastMessage("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");


            this.capturingTeam.getMembers().forEach(player -> {
                PlayerRewardUtil.increaseCoins(player, 10_000);
                PlayerRewardUtil.increaseExperience(player, 5_000);
                PlayerUtil.sendActionText(player, "§a§lGlückwunsch! §fDein Team konnte den Stützpunkt einnehmen.");
            });
        }
    }

    private List<Player> getTeamMembersInStronghold() {
        List<Player> players = this.building.getPlayersInStronghold();
        players.removeIf(player -> !this.capturingTeam.getMembers().contains(player));
        return players;
    }

    private void stopTaskAndChangePhase(final Phase phase) {
        this.checkForPlayersTask.cancel();
        this.building.changePhase(phase);
    }

    public List<Player> getPlayersNearbyCenterLocation() {
        Location center = LocationUtil.getCenterLocation(this.building.getMinPos(), this.building.getMaxPos());
        return center.getWorld().getNearbyEntities(center, 10, 10, 10)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());
    }


    @Override
    public void onStart() {
        List<String> hologramLines = new ArrayList<>();
        hologramLines.add("§8§l§m*-*-*-*-*-*-*-*-*§r §6§lStützpunkte §8§l§m*-*-*-*-*-*-*-*-*");
        hologramLines.add("§eDas Team §a§l" + this.capturingTeam.getName() + " §eversucht den Stützpunkt einzunehmen.");
        hologramLines.add("§eUnterbrich die Einnahme, in dem sich kein Mitglied des Teams im Bereich im Bereich");
        hologramLines.add("§edes Stützpunktes befindet.");
        hologramLines.add(" ");
        hologramLines.add("§eZeit bis zur Einnahme: §c§l" + this.secondsUntilCompleteCapture + " Sekunden");

        Location center = LocationUtil.getCenterLocation(this.building.getMinPos(), this.building.getMaxPos());
        this.hologramService.createDefaultHologram(HOLOGRAM_PHASE_ID, center, hologramLines,
                new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    @Override
    public void onEnd() {

    }

    @Override
    public String getName() {
        return "Einnehmen";
    }
}
