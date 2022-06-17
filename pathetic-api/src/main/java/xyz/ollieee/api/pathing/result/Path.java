package xyz.ollieee.api.pathing.result;

import lombok.NonNull;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

public interface Path {
    
    /**
     * Joins this Path with the given Path.
     * @param path which will be appended at the end.
     * @return {@link Path} the new Path
     */
    Path join(Path path);
    
    /**
     * Returns the path from the Pathfinder as a {@link Iterable} full of {@link PathLocation}
     */
    @NonNull
    Iterable<PathLocation> getLocations();
    
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
