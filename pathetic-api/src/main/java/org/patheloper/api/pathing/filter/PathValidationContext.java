package org.patheloper.api.pathing.filter;

import lombok.Value;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/**
 * PathValidationContext is a data container used during the pathfinding process to provide relevant
 * contextual information needed for evaluating path validity.
 *
 * <p>This context is passed to {@link PathFilter#filter} methods during the pathfinding process and
 * allows filters to validate or invalidate nodes based on the provided context.
 */
@Value
public class PathValidationContext {

  /**
   * The current position being evaluated in the pathfinding process. This represents the position
   * that is being validated by the filter to determine if it can be part of a valid path.
   */
  PathPosition position;

  /**
   * The parent position of the current position. This is the previous node from which the current
   * position was reached. It is used to trace the path and ensure logical continuity between nodes.
   */
  PathPosition parent;

  /**
   * The absolute start position of the pathfinding process. This represents the original starting
   * point of the path and remains constant throughout the algorithm, providing a stable reference.
   */
  PathPosition absoluteStart;

  /**
   * The absolute target position of the pathfinding process. This is the final goal or destination
   * that the pathfinding algorithm is trying to reach. Like the start, it remains constant and
   * provides a clear end-point for the path.
   */
  PathPosition absoluteTarget;

  /**
   * The snapshot manager provides access to world data, such as block information, in the context
   * of the pathfinding process. It is used to retrieve block data from the world at different
   * positions during path validation.
   */
  SnapshotManager snapshotManager;
}
