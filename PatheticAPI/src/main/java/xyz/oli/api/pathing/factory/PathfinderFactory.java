package xyz.oli.api.pathing.factory;

import xyz.oli.api.pathing.Pathfinder;

public interface PathfinderFactory {

    /**
     * Instantiates and returns a new Pathfinder instance
     */
    Pathfinder newPathfinder();
}
