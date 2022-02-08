package xyz.oli.api.pathing.factory;

import xyz.oli.api.pathing.Pathfinder;

public interface PathfinderFactory {

    /**
     * Instantiates a new Pathfinder instance
     * @return new Pathfinder instance
     */
    Pathfinder newPathfinder();
}
