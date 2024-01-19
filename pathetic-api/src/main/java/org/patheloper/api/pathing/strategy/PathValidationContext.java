package org.patheloper.api.pathing.strategy;

import lombok.Value;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/** A parameter object for the {@link PathfinderStrategy#isValid} method. */
@Value
public class PathValidationContext {

  PathPosition position;
  PathPosition parent;
  SnapshotManager snapshotManager;
}
