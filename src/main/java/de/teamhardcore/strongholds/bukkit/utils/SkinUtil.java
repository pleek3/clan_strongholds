package de.teamhardcore.strongholds.bukkit.utils;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class SkinUtil {

    private final String SCHEMATIC_FILE_URL = MineQuestStrongholdPlugin.INSTANCE().getDataFolder() + File.separator + "/schematics/%NAME%.schematic";

    public void paste(final Location location, final String schematicFileName, int rotation) {
        File schematic = new File(SCHEMATIC_FILE_URL.replace("%NAME%", schematicFileName));

        if (!schematic.exists()) {
            System.out.println(
                    "Cannot find schematic with name " + schematicFileName + " in folder : " + SCHEMATIC_FILE_URL.replace(
                            "%NAME%.schematic", ""));
            return;
        }

        EditSession session = MineQuestStrongholdPlugin.INSTANCE().getWorldEditPlugin()
                .getWorldEdit()
                .getEditSessionFactory()
                .getEditSession(new BukkitWorld(location.getWorld()), 10000);

        // EditSession session = new EditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), 10000);
        session.setFastMode(true);

        try {
            CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(schematic).load(schematic);
            clipboard.rotate2D(rotation);
            clipboard.setOrigin(new Vector(location.getX(), location.getY(), location.getZ()));
            clipboard.paste(session, new Vector(location.getX(), location.getY(), location.getZ()), false);
        } catch (MaxChangedBlocksException | DataException | IOException e) {
            e.printStackTrace();
        }
    }

}
