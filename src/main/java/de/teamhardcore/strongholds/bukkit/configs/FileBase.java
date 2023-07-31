package de.teamhardcore.strongholds.bukkit.configs;

import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class FileBase {

    private final static File DATA_FOLDER = MineQuestStrongholdPlugin.INSTANCE().getDataFolder();

    private final String path;
    private final String fileName;
    private File file;
    private FileConfiguration cfg;
    private boolean deletedFlag = false;

    public FileBase(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        reloadConfig();
    }

    public abstract void init();

    public abstract void writeDefaults();

    /**
     * If the file doesn't exist, create it
     */
    public void reloadConfig() {
        if (deletedFlag)
            return;
        if (file == null)
            file = new File(DATA_FOLDER + File.separator + path, fileName + ".yml");
        if (!(file.exists())) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * If the config file has been deleted, return null. If the config file hasn't been loaded yet, load it. If the config
     * file has been loaded, return it
     *
     * @return The config file.
     */
    public FileConfiguration getConfig() {
        if (deletedFlag)
            return null;
        if (cfg == null)
            reloadConfig();
        return cfg;
    }

    /**
     * If the file and config are not null, save the config to the file
     */
    public void saveConfig() {
        if (file == null || cfg == null)
            return;
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If the file exists, delete it
     */
    public void deleteConfig() {
        if (file == null || !(file.exists()))
            return;
        cfg = null;
        deletedFlag = true;
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

}
