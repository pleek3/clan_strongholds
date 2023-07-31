package de.teamhardcore.strongholds.bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.teamhardcore.strongholds.bukkit.configs.ConfigurationContainer;
import de.teamhardcore.strongholds.bukkit.configs.StrongholdConfig;
import de.teamhardcore.strongholds.bukkit.game.listener.PlayerListener;
import de.teamhardcore.strongholds.bukkit.game.service.StrongholdBuildingConfigService;
import de.teamhardcore.strongholds.bukkit.game.service.StrongholdGameService;
import de.teamhardcore.strongholds.bukkit.team.service.MineQuestTeamService;
import de.teamhardcore.strongholds.bukkit.team.service.TeamService;
import de.teamhardcore.strongholds.bukkit.ui.DHAPIHologramService;
import de.teamhardcore.strongholds.bukkit.ui.HologramService;
import de.teamhardcore.strongholds.setup.commands.SetupCommands;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MineQuestStrongholdPlugin extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static MineQuestStrongholdPlugin INSTANCE;

    private ConfigurationContainer configurationContainer;

    private HologramService hologramService;
    private TeamService teamService;
    private StrongholdGameService gameService;
    private StrongholdBuildingConfigService strongholdBuildingConfigService;

    private WorldEditPlugin worldEditPlugin;

    @Override
    public void onEnable() {
        init();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    private void init() {
        this.configurationContainer = new ConfigurationContainer(new StrongholdConfig());
        this.strongholdBuildingConfigService = new StrongholdBuildingConfigService();
        this.hologramService = new DHAPIHologramService();
        this.teamService = new MineQuestTeamService();
        this.gameService = new StrongholdGameService(this.strongholdBuildingConfigService);

        this.worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");

        getServer().getPluginManager().registerEvents(new PlayerListener(this.gameService, this.teamService), this);
        getCommand("clanstrongholdsetup").setExecutor(new SetupCommands(this.gameService, this.worldEditPlugin));
    }

}
