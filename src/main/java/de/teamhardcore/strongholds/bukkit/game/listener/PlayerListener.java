package de.teamhardcore.strongholds.bukkit.game.listener;

import de.teamhardcore.strongholds.bukkit.game.building.Stronghold;
import de.teamhardcore.strongholds.bukkit.game.building.StrongholdCaptureType;
import de.teamhardcore.strongholds.bukkit.game.phases.AttackPhase;
import de.teamhardcore.strongholds.bukkit.game.phases.CollectingPhase;
import de.teamhardcore.strongholds.bukkit.game.service.StrongholdGameService;
import de.teamhardcore.strongholds.bukkit.team.Team;
import de.teamhardcore.strongholds.bukkit.team.service.TeamService;
import de.teamhardcore.strongholds.bukkit.utils.PlayerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final StrongholdGameService gameService;
    private final TeamService teamService;

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item drop = event.getItemDrop();
        Stronghold stronghold = this.gameService.findStrongholdByLocation(drop.getLocation());

        Team team = this.teamService.getTeamForPlayer(player);

        if (team == null) {
            return;
        }

        if (stronghold == null) {
            return;
        }

        if (!stronghold.getConfig().getCaptureType().equals(StrongholdCaptureType.COLLECT_BLOCKS)) {
            return;
        }

        if (!(stronghold.getPhase() instanceof CollectingPhase)) {
            return;
        }

        if (!stronghold.getConfig().isCollectable(drop.getItemStack().getType())) {
            return;
        }

        CollectingPhase phase = (CollectingPhase) stronghold.getPhase();
        phase.collectResourceForTeam(player, team, drop.getItemStack().getType(), drop.getItemStack().getAmount());
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        PlayerUtil.sendActionText(player,
                "§fDu hast §7" + drop.getItemStack().getType().name() + " §a§l" + drop.getItemStack().getAmount() + "x §fhinzugefügt.");

        event.setCancelled(true);
        //todo: remove item from inventory
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block drop = event.getBlock();
        Stronghold stronghold = this.gameService.findStrongholdByLocation(drop.getLocation());

        Team team = this.teamService.getTeamForPlayer(player);

        if (team == null) {
            return;
        }

        if (stronghold == null) {
            return;
        }

        if (!stronghold.getConfig().getCaptureType().equals(StrongholdCaptureType.BREAK_BLOCK)) {
            return;
        }

        if (!(stronghold.getPhase() instanceof CollectingPhase)) {
            return;
        }

        if (!stronghold.getConfig().isCollectable(drop.getType())) {
            return;
        }

        CollectingPhase phase = (CollectingPhase) stronghold.getPhase();
        phase.collectResourceForTeam(player, team, drop.getType(), 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        PlayerUtil.sendActionText(player,
                "§fDu hast §7" + drop.getType().name() + " §a§l" + 1 + "x §fhinzugefügt.");

        event.setCancelled(true);
        //todo: remove item from inventory
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR)) return;

        Player player = event.getPlayer();
        Stronghold stronghold = this.gameService.findStrongholdByLocation(event.getClickedBlock().getLocation());
        Team team = this.teamService.getTeamForPlayer(player);

        if (team == null) {
            return;
        }

        if (stronghold == null) {
            return;
        }

        if (!(stronghold.getPhase() instanceof AttackPhase)) {
            return;
        }


        AttackPhase phase = (AttackPhase) stronghold.getPhase();
        phase.attackBuilding(player);
        event.setCancelled(true);
    }
}
