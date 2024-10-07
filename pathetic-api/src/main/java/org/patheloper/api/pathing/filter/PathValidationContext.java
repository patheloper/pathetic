package org.patheloper.api.pathing.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/** A parameter object for the {@link PathFilter#filter} method. */
@Getter
@AllArgsConstructor
public class PathValidationContext {

  /**
   * The target block that is being evaluated. This block may be modified by the filter to influence
   * the pathfinding process.
   */
  @Setter(AccessLevel.PACKAGE) private PathPosition targetPosition;

  /**
   * The parent block of the target block. This block is the block that the target block was
   * evaluated from.
   */
  private final PathPosition parentPosition;

  /**
   * The snapshot manager that can be used to query the state of the world at the time of the
   * pathfinding request.
   */
  private final SnapshotManager snapshotManager;
}
