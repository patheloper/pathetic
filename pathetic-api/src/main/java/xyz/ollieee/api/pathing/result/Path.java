package xyz.ollieee.api.pathing.result;

import lombok.NonNull;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

public interface Path {

    /**
     * Interpolates the points of this Path using the de Boor's algorithm
     * @see #getLocations
     * @return a newly created Path with interpolated points formed to a cubic B-spline
     */
    Path interpolate();

    /**
     * Joins this Path with the given Path.
     * @param path which will be appended at the end.
     * @return {@link Path} the new Path
     */
    Path join(Path path);

    /**
     * Trims this Path to the given length.
     * @param length the length to which the Path will be trimmed.
     * @return {@link Path} the new Path
     */
    Path trim(int length);

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
    PathLocation getEnd();
}
