package xyz.oli.pathing;

import org.bukkit.Location;

import java.util.LinkedHashSet;

public interface Path {
    /**
     * Returns the path from the Pathfinder
     *
     * @return LinkedHashSet<Location> The locations of the pathfinding
     */
    LinkedHashSet<Location> getPath();

    /**
     * Returns the start location of the path
     *
     * @return Location the start location
     */
    Location getStart();

    /**
     * Returns the end location of the path
     *
     * @return Location the end location
     */
    Location getEnd();
}
