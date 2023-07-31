package de.teamhardcore.strongholds.setup.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import de.teamhardcore.strongholds.bukkit.configs.BuildingConfig;
import de.teamhardcore.strongholds.bukkit.game.building.Stronghold;
import de.teamhardcore.strongholds.bukkit.game.service.StrongholdGameService;
import de.teamhardcore.strongholds.bukkit.utils.LocationUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetupCommands implements CommandExecutor {

    private final static String ADMIN_HELP_PERMISSION = "minequest.stronghold.admin";

    private final StrongholdGameService strongholdGameService;
    private final WorldEditPlugin worldEditPlugin;

    public SetupCommands(final StrongholdGameService gameService, final WorldEditPlugin worldEditPlugin) {
        this.strongholdGameService = gameService;
        this.worldEditPlugin = worldEditPlugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player player = (Player) cs;

        if (args.length == 0 || args.length > 5) {
            sendHelp(player);
            return true;
        }

        if (args.length == 1) {
            listAllBuildingConfigs(player);
            return true;
        }

        String strongholdName = args[1];

        if (args.length == 2) {
            switch (args[0]) {
                case "create":
                    createBuildingConfig(player, strongholdName);
                    break;
                case "delete":
                    deleteBuildingConfig(player, strongholdName);
                    break;
                case "info":
                    getInformationAboutBuildingConfig(player, strongholdName);
                    break;
                case "set":
                    setBuildingConfigLocation(player, strongholdName);
                    break;
                default:
                    sendHelp(player);
            }
        }

        if (args.length == 3) {
            if (args[0].equals("removeResource")) {
                removeCollectableResource(player, strongholdName);
            } else {
                sendHelp(player);
            }
        }

        if (args.length == 4) {
            if (args[0].equals("setResource")) {
                int resourceAmount;

                try {
                    resourceAmount = Integer.parseInt(args[2]);

                    if (resourceAmount <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    player.sendMessage("§cDu musst eine richtige Zahl angeben.");
                    return true;
                }


                setCollectableResource(player, strongholdName, resourceAmount);
            } else {
                sendHelp(player);
            }
        }

        return true;
    }

    public void sendHelp(Player player) {
        if (!player.hasPermission(ADMIN_HELP_PERMISSION)) {
            player.sendMessage(
                    "§7Entdecke verstreute Clan-Stützpunkte auf der Map, jedes mit seinen eigenen einzigartigen Anforderungen an Ressourcen für den Aufbau. Errichte mutig und geschickt deinen Clan-Stützpunkt, verteidige ihn gegen anstürmende Feinde und öffne die Tür zu einem Meer an unwiderstehlichen Belohnungen und unbestreitbaren Vorteilen für dich und deinen Clan. Sei derjenige, der sich dem Aufstieg nicht entziehen kann und zeige der Welt, dass dein Clan unaufhaltsam ist.");
            return;
        }

        player.sendMessage("§cSetup-Befehle für die Clan-Stützpunkte: ");
        player.sendMessage("§8- §c/clanstrongholdsetup §flist");
        player.sendMessage("§8- §c/clanstrongholdsetup §fcreate <Stützpunktname>");
        player.sendMessage("§8- §c/clanstrongholdsetup §fdelete <Stützpunktname>");
        player.sendMessage("§8- §c/clanstrongholdsetup §finfo <Stützpunktname>");
        player.sendMessage(" ");
        player.sendMessage("§8- §c/clanstrongholdsetup §fsetResource <Stützpunktname> <Material> <Anzahl>");
        player.sendMessage("§8- §c/clanstrongholdsetup §fremoveResource <Stützpunktname> <Material> <Anzahl>");
        player.sendMessage(" ");
        player.sendMessage("§8- §c/clanstrongholdsetup §fset <Stützpunktname>");
    }

    public void createBuildingConfig(final Player player, final String name) {
        Stronghold stronghold = this.strongholdGameService.findStrongholdByName(name);

        if (stronghold != null) {
            player.sendMessage("§cDieser Clan Stützpunkt existiert bereits.");
            return;
        }

        BuildingConfig config = new BuildingConfig(name);
        config.saveConfig();
        player.sendMessage("§eDu hast erfolgreich die Konfiguration für " + name + " erstellt.");
    }

    public void deleteBuildingConfig(final Player player, final String name) {
        Stronghold stronghold = this.strongholdGameService.findStrongholdByName(name);

        if (stronghold == null) {
            player.sendMessage("§cDieser Clan Stützpunkt existiert nicht.");
            return;
        }

        this.strongholdGameService.removeStronghold(stronghold);

        BuildingConfig config = stronghold.getConfig();
        config.deleteConfig();
    }

    public void getInformationAboutBuildingConfig(final Player player, final String name) {
        Stronghold stronghold = this.strongholdGameService.findStrongholdByName(name);

        if (stronghold == null) {
            player.sendMessage("§cDieser Clan Stützpunkt existiert nicht.");
            return;
        }

        BuildingConfig config = stronghold.getConfig();

        player.sendMessage("§cInformationen über den §7" + stronghold.getName() + "-Stützpunkt§8: ");
        player.sendMessage("§cLocation: §f" + LocationUtil.convertLocationToString(stronghold.getMaxPos()));
        player.sendMessage("§cAktuelle Phase§8: §f" + stronghold.getPhase().getName());
        player.sendMessage(" ");
        player.sendMessage("§cNotwendigen Ressourcen§8: ");

        for (final Material material : config.getCollectableMaterials()) {
            int amountToCollect = config.getAmountOfResourcesToCollect().get(material);
            player.sendMessage(" §8- §f" + amountToCollect + "x §c" + material.name());
        }
    }

    public void listAllBuildingConfigs(final Player player) {
        player.sendMessage("§cAlle verfügbaren Stützpunkte: ");
        player.sendMessage(" ");
        for (final Stronghold stronghold : this.strongholdGameService.getStrongholds()) {
            player.sendMessage(
                    " §8- §c" + stronghold.getName() + " §7<-> §f" + LocationUtil.convertLocationToString(
                            stronghold.getMaxPos()));
        }
    }

    public void setBuildingConfigLocation(final Player player, final String name) {
        Stronghold stronghold = this.strongholdGameService.findStrongholdByName(name);

        if (stronghold != null) {
            this.strongholdGameService.removeStronghold(stronghold);
        }

        Selection selection = this.worldEditPlugin.getSelection(player);

        if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
            player.sendMessage("§cDu musst die Blockregion des Stützpunktes mit WorldEdit auswählen.");
            return;
        }

        BuildingConfig config = new BuildingConfig(name);
        config.setMaxPos(selection.getMaximumPoint());
        config.setMinPos(selection.getMinimumPoint());
        config.saveConfig();

        this.strongholdGameService.addStronghold(new Stronghold(config));
    }

    public void setCollectableResource(final Player player, final String name, final int amount) {
        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            player.sendMessage("§cDu musst ein Item in der Hand halten.");
            return;
        }

        Material material = itemStack.getType();

        Stronghold stronghold = this.strongholdGameService.findStrongholdByName(name);

        if (stronghold == null) {
            player.sendMessage("§cDieser Clan Stützpunkt existiert nicht.");
            return;
        }

        BuildingConfig config = stronghold.getConfig();

        config.getAmountOfResourcesToCollect().put(material, amount);
        if (config.getCollectableMaterials().contains(material)) return;
        config.getCollectableMaterials().add(material);
        config.saveConfig();

        player.sendMessage("§aDie Konfiguration für den Clan Stützpunkt " + name + " wurde aktualsisiert.");
    }

    public void removeCollectableResource(final Player player, final String name) {
        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            player.sendMessage("§cDu musst ein Item in der Hand halten.");
            return;
        }

        Material material = itemStack.getType();
        Stronghold stronghold = this.strongholdGameService.findStrongholdByName(name);

        if (stronghold == null) {
            player.sendMessage("§cDieser Clan Stützpunkt existiert nicht.");
            return;
        }

        BuildingConfig config = stronghold.getConfig();

        config.getAmountOfResourcesToCollect().remove(material);
        config.getCollectableMaterials().remove(material);
        config.saveConfig();
        player.sendMessage("§aDie Konfiguration für den Clan Stützpunkt " + name + " wurde aktualsisiert.");
    }

}
