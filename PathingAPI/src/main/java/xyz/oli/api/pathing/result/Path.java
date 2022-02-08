package xyz.oli.api.pathing.result;

import xyz.oli.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

public interface Path {
    
    /**
     * Returns the path from the Pathfinder
     *
     * @return LinkedHashSet<Location> The locations of the pathfinding
     */
    LinkedHashSet<PathLocation> getLocations();

    /**
     * Returns the start location of the path
     *
     * @return Location the start location
     */
    PathLocation getStart();

    /**
     * Returns the end location of the path
     *
     * @return Location the end location
     */
    PathLocation getEnd();
}
