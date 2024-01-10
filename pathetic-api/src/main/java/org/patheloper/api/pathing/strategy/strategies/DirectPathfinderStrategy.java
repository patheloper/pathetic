package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/** A {@link PathfinderStrategy} to find the direct path to a given endpoint */
public class DirectPathfinderStrategy implements PathfinderStrategy {

  @Override
  public boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager) {
    return snapshotManager.getBlock(position).isPassable();
  }
}
