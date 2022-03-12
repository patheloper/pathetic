package xyz.oli.pathing.model.finder;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;

import xyz.oli.api.pathing.result.Path;
import xyz.oli.api.wrapper.PathLocation;
import xyz.oli.api.wrapper.PathVector;
import xyz.oli.pathing.Pathetic;
import xyz.oli.pathing.model.PathImpl;
import xyz.oli.utils.BukkitConverter;

import java.util.*;

public class LOSPathfinder {

    public static Optional<Path> tryDirect(PathLocation start, PathLocation target) {

        Pathetic.getPluginLogger().info("Using LOS");

        Location location = BukkitConverter.toLocation(start);
        RayTraceResult result = location.getWorld().rayTraceBlocks(location, BukkitConverter.toVector(target.toVector().subtract(start.toVector())), start.distance(target) + 1, FluidCollisionMode.ALWAYS, false);

        Pathetic.getPluginLogger().info(result == null ? "null" : result.toString());

        if (result == null) {
            return Optional.of(new PathImpl(start, target, new LinkedHashSet<>(line(start, target))));
        }
        return Optional.empty();
    }

    private static List<PathLocation> line(final PathLocation start, final PathLocation end) {

        final PathVector fullLine = end.toVector().subtract(start.toVector());
        final double fullLength = fullLine.length();
        final PathVector deltaLine = fullLine.clone().normalize();

        final List<PathLocation> locations = new ArrayList<>((int) (fullLength));

        locations.add(start);

        for (int i = 0; i < (int) (fullLength); i++) {
            final PathLocation nextLocation = locations.get(locations.size() - 1).clone().add(deltaLine);
            locations.add(nextLocation);
        }

        locations.remove(start);
        locations.add(end);
        return locations;
    }

}
