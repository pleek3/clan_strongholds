package de.teamhardcore.strongholds.bukkit.ui;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class DHAPIHologramService implements HologramService {

    public DHAPIHologramService() {
    }

    @Override
    public void createDefaultHologram(String id, Location location, List<String> lines, List<Player> players) {
        if (DHAPI.getHologram(id) != null) {
            removeHologram(id);
        }

        Hologram hologram = DHAPI.createHologram(id, location.clone().add(0, 2.0, 0), false, lines);
        hologram.showAll();
    }

    @Override
    public void removeHologram(String id) {
        Hologram hologram = DHAPI.getHologram(id);

        if (hologram == null) {
            return;
        }

        hologram.destroy();
    }

    @Override
    public void updateLine(String id, int index, String line) {
        Hologram hologram = DHAPI.getHologram(id);

        if (hologram == null) {
            return;
        }

        HologramPage page = hologram.getPage(0);
        page.getLine(index).setText(line);
        page.getLine(index).update(); //update for all viewing players
    }
}
