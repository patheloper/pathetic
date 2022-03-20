package xyz.ollieee.api.pathing.factory;

import xyz.ollieee.api.pathing.Pathfinder;

public interface PathfinderFactory {

    /**
     * Instantiates and returns a new {@link Pathfinder} instance
     */
    Pathfinder newPathfinder();
}
