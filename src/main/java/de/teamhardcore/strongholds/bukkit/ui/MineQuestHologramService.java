package de.teamhardcore.strongholds.bukkit.ui;

import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.HologramBuilder;
import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.line.ILine;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.placeholder.Placeholders;
import de.teamhardcore.strongholds.bukkit.MineQuestStrongholdPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class MineQuestHologramService implements HologramService {

    private final Map<String, Hologram> holograms = new HashMap<>();

    private final MineQuestStrongholdPlugin plugin;
    private HologramPool hologramPool;

    @Override
    public void createDefaultHologram(final String id, final Location location, final List<String> lines, List<Player> players) {
        if (this.holograms.containsKey(id)) {
            removeHologram(id);
        }

        HologramBuilder builder = Hologram.builder(plugin, location, new Placeholders(Placeholders.STRING));
        lines.forEach(builder::addTextLine);
        Hologram hologram = builder.loadAndBuild(getDefaultPool());
        players.forEach(hologram::show);

        this.holograms.put(id, hologram);
    }

    @Override
    public void removeHologram(final String id) {
        Hologram hologram = this.holograms.get(id);

        if (hologram == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ILine<?> line : hologram.getLines()) {
                line.hide(player);
            }
        }


        Bukkit.getOnlinePlayers().forEach(hologram::hide);
        this.holograms.remove(id);
    }

    @Override
    public void updateLine(final String id, final int index, final String text) {
        Hologram hologram = this.holograms.get(id);

        if (hologram == null) {
            return;
        }

        if (hologram.getLines().size() <= index) {
            return;
        }

        ILine<?> line = hologram.getLines().get(index);

        if (line == null) {
            return;
        }

        if (!(line instanceof TextLine)) {
            return;
        }

        ((TextLine) line).asTextLine().setObj(text);
        Bukkit.getOnlinePlayers().forEach(line::update);
    }

    private HologramPool getDefaultPool() {
        if (this.hologramPool == null) {
            this.hologramPool = new HologramPool(this.plugin, 100);
        }
        return this.hologramPool;
    }

}
