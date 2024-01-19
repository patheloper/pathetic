package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;

/** A {@link PathfinderStrategy} to find the direct path to a given endpoint */
public class DirectPathfinderStrategy implements PathfinderStrategy {

  @Override
  public boolean isValid(@NonNull PathValidationContext pathValidationContext) {
    return pathValidationContext
        .getSnapshotManager()
        .getBlock(pathValidationContext.getPosition())
        .isPassable();
  }
}
