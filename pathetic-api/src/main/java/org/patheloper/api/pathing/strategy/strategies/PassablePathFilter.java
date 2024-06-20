package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.pathing.strategy.PathFilter;

/** A {@link PathFilter} to find the direct path to a given endpoint */
public class PassablePathFilter implements PathFilter {

  @Override
  public boolean filter(@NonNull PathValidationContext pathValidationContext) {
    return pathValidationContext
        .getSnapshotManager()
        .getBlock(pathValidationContext.getPosition())
        .isPassable();
  }
}
