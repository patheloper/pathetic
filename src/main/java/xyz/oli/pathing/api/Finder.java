package xyz.oli.pathing.api;

import org.bukkit.Location;
import xyz.oli.pathing.api.finder.PathFinder;
import xyz.oli.pathing.api.finder.PathResult;
import xyz.oli.pathing.api.strategy.WalkingPathfinderStrategy;

public interface Finder {

    PathFinder pathfinder = new PathFinder();

    static Path findPath(Location from, Location to) {

        PathResult path = pathfinder.findPath(from, to, new WalkingPathfinderStrategy());
        return path.path();
    }
}
