package org.patheloper.api.pathing.filter;

import lombok.Value;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A context object that holds relevant parameters for path validation, used by the {@link
 * PathFilter#filter} method.
 */
@Value
public class PathValidationContext {

  /** The current {@link PathPosition} being validated in the pathfinding process. */
  PathPosition position;

  /** The parent {@link PathPosition} from which the current position was reached. */
  PathPosition parent;

  /** The {@link SnapshotManager} used to access historical state snapshots during validation. */
  SnapshotManager snapshotManager;
}
