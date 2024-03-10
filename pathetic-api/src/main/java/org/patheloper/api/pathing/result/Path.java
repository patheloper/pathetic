package org.patheloper.api.pathing.result;

import lombok.NonNull;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathPosition;

import java.util.function.Consumer;

public interface Path extends Iterable<PathPosition> {

  /**
   * The length of the Path compiled from the number of positions
   *
   * @return the length of the path
   */
  int length();

  /**
   * Interpolates the positions of this Path to a new Path with the given resolution.
   *
   * <p>The resulting path will have additional positions inserted between consecutive positions in
   * the original path such that no two consecutive positions are more than `resolution` blocks
   * apart. The interpolated positions are computed using linear interpolation, which means that the
   * resulting path will have a smooth curve that passes through each of the original positions.
   *
   * @param resolution The desired distance between consecutive positions in the resulting path, in
   *     blocks. A lower value will result in a higher resolution and a smoother curve, but will
   *     also increase the number of positions in the resulting path and possibly reduce
   *     performance.
   * @return a newly created Path with interpolated positions.
   * @see #getPositions
   */
  Path interpolate(double resolution);

  /**
   * Simplifies the path by removing intermediate positions based on the given epsilon value. The
   * start and end positions are always included in the simplified path.
   *
   * @param epsilon the epsilon value representing the fraction of positions to keep (should be in
   *     the range greater than 0.0 to 1.0, inclusive)
   * @return a simplified path containing a subset of positions from the original path
   * @throws IllegalArgumentException if epsilon is not in the range greater than 0.0 to 1.0,
   *     inclusive
   */
  Path simplify(double epsilon);

  /**
   * Joins this Path with the given Path.
   *
   * @param path which will be appended at the end.
   * @return {@link Path} the new Path
   */
  Path join(Path path);

  /**
   * Trims this Path to the given length.
   *
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
   *
   * @return {@link PathPosition} The position of the start
   */
  @NonNull
  PathPosition getStart();

  /**
   * Returns the target position of the path
   *
   * @return {@link PathPosition} The position of the target
   */
  @NonNull
  PathPosition getEnd();

  /**
   * Returns the path from the Pathfinder as a {@link Iterable} full of {@link PathPosition}
   * @deprecated Will be removed in future versions {@link #forEach(Consumer)}
   */
  @NonNull
  @Deprecated
  Iterable<PathPosition> getPositions();
}
