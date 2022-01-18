package xyz.oli.pathing.api;

import org.bukkit.Location;
import xyz.oli.pathing.model.path.Path;
import xyz.oli.pathing.model.path.finder.Pathfinder;
import xyz.oli.pathing.model.path.finder.PathfinderResult;
import xyz.oli.pathing.model.path.finder.strategy.strategies.WalkingPathfinderStrategy;

public interface Finder {

    Pathfinder pathfinder = new Pathfinder();

    static Path findPath(Location from, Location to) {

        PathfinderResult path = pathfinder.findPath(from, to, new WalkingPathfinderStrategy());
        return path.getPath();
    }
}
