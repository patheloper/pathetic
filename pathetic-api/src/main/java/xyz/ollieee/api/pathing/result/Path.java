package xyz.ollieee.api.pathing.result;

import lombok.NonNull;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

public interface Path {
    
    /**
     * Returns the path from the Pathfinder as a {@link LinkedHashSet} full of {@link PathLocation}
     */
    @NonNull
    LinkedHashSet<PathLocation> getLocations();

    /**
     * Returns the start location of the path
     * @return {@link PathLocation} The location of the start
     */
    @NonNull
    PathLocation getStart();

    /**
     * Returns the target location of the path
     * @return {@link PathLocation} The location of the target
     */
    @NonNull
    PathLocation getTarget();
}
