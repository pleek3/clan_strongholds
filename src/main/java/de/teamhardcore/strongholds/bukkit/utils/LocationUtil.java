package de.teamhardcore.strongholds.bukkit.utils;


import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@UtilityClass
public class LocationUtil {
    private final Random RANDOM = new Random();

    public List<Location> generateRandomLocations(Location origin, double maxDeviation, int locationsAmounts) {
        List<Location> randomLocations = new ArrayList<>();

        double xDeviation = RANDOM.nextDouble() * maxDeviation;
        double yDeviation = RANDOM.nextDouble() * maxDeviation;
        double zDeviation = RANDOM.nextDouble() * maxDeviation;

        for (int i = 0; i < locationsAmounts; i++) {
            double randomX = origin.getX() + getRandomSign() * xDeviation;
            double randomY = origin.getY() + getRandomSign() * yDeviation;
            double randomZ = origin.getZ() + getRandomSign() * zDeviation;

            Location randomLocation = new Location(origin.getWorld(), randomX, randomY, randomZ);
            randomLocations.add(randomLocation);
        }

        return randomLocations;
    }

    private int getRandomSign() {
        return RANDOM.nextBoolean() ? 1 : -1;
    }

    public boolean isInside(final Location min, final Location max, final Location locationC) {
        World world = locationC.getWorld();
        double x = locationC.getX();
        double y = locationC.getY();
        double z = locationC.getZ();

        boolean isInsideX = min.getX() <= x && x <= max.getX();
        boolean isInsideY = min.getY() <= y && y <= max.getY();
        boolean isInsideZ = min.getZ() <= z && z <= max.getZ();

        return world.equals(min.getWorld()) && isInsideX && isInsideY && isInsideZ;
    }

    public Location getCenterLocation(Location loc1, Location loc2) {
        World world = loc1.getWorld();
        double x1 = loc1.getX();
        double y1 = loc1.getY();
        double z1 = loc1.getZ();
        double x2 = loc2.getX();
        double y2 = loc2.getY();
        double z2 = loc2.getZ();

        double midpointX = (x1 + x2) / 2.0;
        double midpointY = (y1 + y2) / 2.0;
        double midpointZ = (z1 + z2) / 2.0;

        return new Location(world, midpointX, midpointY, midpointZ);
    }

    public String convertLocationToString(final Location location) {
        World world = location.getWorld();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

    public Location convertStringToLocation(final String data) {
        String[] split = data.split(";");

        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);

        Location location = new Location(world, x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }

}
