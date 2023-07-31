package de.teamhardcore.strongholds.bukkit.utils;

import org.bukkit.entity.Player;

public class PlayerRewardUtil {

    public static void increaseCoins(final Player player, final long amount) {
        player.sendMessage("§eDu hast §7" + amount + " §eMünzen erhalten.");
    }

    public static void increaseExperience(final Player player, final long amount) {
        player.sendMessage("§eDu hast §7" + amount + " §eErfahrungspunkte erhalten.");
    }

}
