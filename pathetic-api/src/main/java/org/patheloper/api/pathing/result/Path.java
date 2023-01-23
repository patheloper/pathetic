package org.patheloper.api.pathing.result;

import lombok.NonNull;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathPosition;

public interface Path {

    /**
     * The length of the Path compiled from the number of positions
     * @return the length of the path
     */
    int length();

    /**
     * Interpolates the points of this Path using a spline algorithm
     *
     * @param nthBlock   Will use every nth block of the path as a control point
     * @param resolution The resolution of the interpolation (in blocks). The higher the resolution, the more points will be interpolated
     * @return a newly created Path with interpolated points
     * @see #getPositions
     */
    Path interpolate(int nthBlock, double resolution);

    /**
     * Enlarges the path by filling in the spaces between the points and adding new points in between based on the given resolution
     * @param resolution The resolution of the enlargement (in blocks). The lower the resolution, the more points will be added
     *                   between the existing points
     */
    Path enlarge(double resolution);

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
     * Mutates each of the positions in the path with the given consumer
     *
     * @param mutator the {@link ParameterizedSupplier} to mutate the positions with
     * @return {@link Path} the new Path
     */
    @NonNull
    Path mutatePositions(ParameterizedSupplier<PathPosition> mutator);

    /**
     * Returns the start position of the path
     * @return {@link PathPosition} The position of the start
     */
    @NonNull
    PathPosition getStart();

    /**
     * Returns the target position of the path
     * @return {@link PathPosition} The position of the target
     */
    @NonNull
    PathPosition getEnd();

    /**
     * Returns the path from the Pathfinder as a {@link Iterable} full of {@link PathPosition}
     */
    @NonNull
    Iterable<PathPosition> getPositions();
}
