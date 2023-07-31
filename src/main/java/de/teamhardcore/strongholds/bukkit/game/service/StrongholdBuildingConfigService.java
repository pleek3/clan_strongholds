package de.teamhardcore.strongholds.bukkit.game.service;

import de.teamhardcore.strongholds.bukkit.configs.BuildingConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StrongholdBuildingConfigService {

    public Set<BuildingConfig> loadBuildingConfigs(String directory) {
        try (Stream<Path> stream = Files.list(Paths.get(directory))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(BuildingConfig::new)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

}
