package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.pathing.strategy.PathFilter;
import org.patheloper.api.wrapper.PathBlock;

/**
 * A PathFilter implementation that determines if a path is passable.
 *
 * @see PathBlock#isPassable()
 */
public class PassablePathFilter implements PathFilter {

  @Override
  public boolean filter(@NonNull PathValidationContext pathValidationContext) {
    return pathValidationContext
        .getSnapshotManager()
        .getBlock(pathValidationContext.getPosition())
        .isPassable();
  }
}
