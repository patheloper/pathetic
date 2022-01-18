package xyz.oli.pathing.model.path.finder.strategy;

import org.bukkit.Location;

public abstract class PathfinderStrategy {

    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param location The location to check
     * @param previous The previous location
     * @param previouser The previous previous location
     */
    public abstract boolean isValid(Location location, Location previous, Location previouser);

    /**
     * Implement the logic to see if a start/target is valid
     *
     * @param location The location to check
     */
    public abstract boolean verifyEnd(Location location);
}
