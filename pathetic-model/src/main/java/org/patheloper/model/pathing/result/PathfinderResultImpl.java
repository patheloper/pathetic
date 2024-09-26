package org.patheloper.model.pathing.result;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;

/**
 * Implementation of the {@link PathfinderResult} interface that holds the path state and the
 * corresponding path result of a pathfinding operation.
 */
@AllArgsConstructor
public class PathfinderResultImpl implements PathfinderResult {

  private final PathState pathState;
  private final Path path;

  /**
   * Determines if the pathfinding operation was successful.
   *
   * @return {@code true} if the path was successfully found, otherwise {@code false}
   */
  @Override
  public boolean successful() {
    return isPathFound();
  }

  /**
   * Determines if the pathfinding operation failed.
   *
   * @return {@code true} if the operation failed due to failure, limited length, or max iterations
   *     reached
   */
  @Override
  public boolean hasFailed() {
    return isPathFailure();
  }

  /**
   * Determines if the pathfinding operation fell back to a secondary or less optimal path.
   *
   * @return {@code true} if a fallback path was used, otherwise {@code false}
   */
  @Override
  public boolean hasFallenBack() {
    return isFallback();
  }

  /**
   * Retrieves the state of the pathfinding operation.
   *
   * @return the {@link PathState} representing the current state of the pathfinding operation
   */
  @Override
  @NonNull
  public PathState getPathState() {
    return this.pathState;
  }

  /**
   * Retrieves the resulting path of the pathfinding operation.
   *
   * @return the {@link Path} that was generated from the pathfinding process
   */
  @NonNull
  @Override
  public Path getPath() {
    return this.path;
  }

  /**
   * Checks if the path state indicates a successful pathfinding operation.
   *
   * @return {@code true} if the path state is {@link PathState#FOUND}, otherwise {@code false}
   */
  private boolean isPathFound() {
    return pathState == PathState.FOUND;
  }

  /**
   * Checks if the path state indicates failure due to various reasons (failure, length limit, or
   * max iterations).
   *
   * @return {@code true} if the pathfinding operation failed, otherwise {@code false}
   */
  private boolean isPathFailure() {
    return pathState == PathState.FAILED
        || pathState == PathState.LENGTH_LIMITED
        || pathState == PathState.MAX_ITERATIONS_REACHED;
  }

  /**
   * Checks if the pathfinding operation resulted in a fallback path.
   *
   * @return {@code true} if the path state is {@link PathState#FALLBACK}, otherwise {@code false}
   */
  private boolean isFallback() {
    return pathState == PathState.FALLBACK;
  }
}
