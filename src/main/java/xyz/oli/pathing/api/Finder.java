package xyz.oli.pathing.api;

import org.bukkit.Location;
import xyz.oli.pathing.api.finder.PathFinder;
import xyz.oli.pathing.api.finder.PathResult;

public interface Finder {

    PathFinder pathfinder = new PathFinder();

    static Path findPath(Location from, Location to) {

        PathResult path = pathfinder.findPath(from, to);
        return path.path();
    }
}
