package org.patheloper.api.pathing.filter.filters;

import lombok.NonNull;
import org.patheloper.api.pathing.filter.FilterOutcome;
import org.patheloper.api.pathing.filter.FilterResult;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.wrapper.PathBlock;

/**
 * A PathFilter implementation that determines if a path is passable.
 *
 * @see PathBlock#isPassable()
 */
public class PassablePathFilter implements PathFilter {

  @Override
  public FilterOutcome filter(@NonNull PathValidationContext pathValidationContext) {
    PathBlock block =
        pathValidationContext
            .getSnapshotManager()
            .getBlock(pathValidationContext.getTargetPosition());

    if (block.isPassable()) {
      return new FilterOutcome(FilterResult.PASS, pathValidationContext.getTargetPosition());
    } else {
      return new FilterOutcome(FilterResult.FAIL, pathValidationContext.getTargetPosition());
    }
  }
}
