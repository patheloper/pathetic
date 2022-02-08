package xyz.oli.api.pathing;

import xyz.oli.api.options.PathfinderOptions;

public interface PathfinderFactory {

    /**
     * Instantiates a new Pathfinder instance
     * @return new Pathfinder instance
     */
    Pathfinder newPathfinder(PathfinderOptions options);
}
