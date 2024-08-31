package org.patheloper.api.pathing.filter.filters;

import lombok.NonNull;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;

/**
 * A PathFilter implementation that determines if a path is on solid ground.
 */
public class SolidGroundPathFilter implements PathFilter {

  @Override
  public boolean filter(@NonNull PathValidationContext pathValidationContext) {
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();
    PathBlock block = snapshotManager.getBlock(pathValidationContext.getPosition());
    return hasGround(block, snapshotManager);
  }

  protected boolean hasGround(PathBlock block, SnapshotManager snapshotManager) {
    PathBlock below = snapshotManager.getBlock(block.getPathPosition().add(0, -1, 0));
    return below.isSolid();
  }
}
