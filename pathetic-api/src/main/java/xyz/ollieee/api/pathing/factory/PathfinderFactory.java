package xyz.ollieee.api.pathing.factory;

import xyz.ollieee.api.pathing.Pathfinder;

public interface PathfinderFactory {

    /**
     * Instantiates and returns a new Pathfinder instance
     */
    Pathfinder newPathfinder();
}
