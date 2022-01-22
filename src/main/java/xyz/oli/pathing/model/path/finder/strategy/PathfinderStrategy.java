package xyz.oli.pathing.model.path.finder.strategy;

import xyz.oli.pathing.model.wrapper.PathBlock;

public abstract class PathfinderStrategy {

    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param location The location to check
     * @param previous The previous location
     * @param previouser The previous previous location
     */
    public abstract boolean isValid(PathBlock location, PathBlock previous, PathBlock previouser);

    /**
     * Implement the logic to see if a start/target is valid
     *
     * @param location The location to check
     */
    public abstract boolean verifyEnd(PathBlock location);
}
