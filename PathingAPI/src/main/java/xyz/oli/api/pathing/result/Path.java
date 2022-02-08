package xyz.oli.api.pathing.result;

import xyz.oli.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

public interface Path {
    
    /**
     * Returns the path from the Pathfinder as a {@link LinkedHashSet} full of {@link PathLocation}
     */
    LinkedHashSet<PathLocation> getLocations();

    /**
     * Returns the start location of the path
     */
    PathLocation getStart();

    /**
     * Returns the target location of the path
     */
    PathLocation getTarget();
}
