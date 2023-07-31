package de.teamhardcore.strongholds.bukkit.configs;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The main configuration containing all settings for the game itself.
 */

@Getter
public class StrongholdConfig extends FileBase {

    private int takingTime = 10;
    private int cancelCaptureTime = 10;

    public StrongholdConfig() {
        super("", "stronghold_config");
        init();
    }

    @Override
    public void init() {
        writeDefaults();
        loadTakingTime();
    }

    @Override
    public void writeDefaults() {
        FileConfiguration configuration = getConfig();
        configuration.addDefault("takingTime", 10);
        configuration.addDefault("cancelCaptureTime", 10);
        configuration.options().copyDefaults(true);
        saveConfig();
    }

    private void loadTakingTime() {
        if (!getConfig().contains("takingTime")) {

            return;
        }

        this.takingTime = getConfig().getInt("takingTime");
    }

    private void loadCancelCaptureTime() {
        if (!getConfig().contains("cancelCaptureTime")) {
            return;
        }

        this.cancelCaptureTime = getConfig().getInt("cancelCaptureTime");
    }

}
