package xyz.ollieee.api.pathing.result;

import lombok.NonNull;
import xyz.ollieee.api.wrapper.PathLocation;

public interface Path {

    /**
     * Interpolates the points of this Path using a spline algorithm
     *
     * @param nthBlock   Will use every nth block of the path as a control point
     * @param resolution The resolution of the interpolation (in blocks). The higher the resolution, the more points will be interpolated
     * @return a newly created Path with interpolated points
     * @see #getLocations
     * @deprecated Experimental
     */
    @Deprecated
    Path interpolate(int nthBlock, double resolution);

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
     * The length of the Path compiled from the number of locations
     * @return the length of the path
     */
    int length();

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

    /**
     * Returns the path from the Pathfinder as a {@link Iterable} full of {@link PathLocation}
     */
    @NonNull
    Iterable<PathLocation> getLocations();
}
