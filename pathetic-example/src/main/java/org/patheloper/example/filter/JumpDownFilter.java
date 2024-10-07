package org.patheloper.example.filter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.patheloper.api.pathing.filter.FilterOutcome;
import org.patheloper.api.pathing.filter.FilterResult;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/** A PathFilter implementation that allows jumping down to valid surfaces within a height limit. */
public class JumpDownFilter implements PathFilter {

  private final int maxJumpHeight;

  /**
   * Constructor to initialize the filter with a maximum jump height.
   *
   * @param maxJumpHeight The maximum height that a node can jump down.
   */
  public JumpDownFilter(int maxJumpHeight) {
    this.maxJumpHeight = maxJumpHeight;
  }

  @Override
  public FilterOutcome filter(@NonNull PathValidationContext pathValidationContext) {
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();
    PathPosition currentPosition = pathValidationContext.getTargetPosition();

    for (int i = 1; i <= maxJumpHeight; i++) {
      PathBlock belowBlock = snapshotManager.getBlock(currentPosition.add(0, -i, 0));

      if (belowBlock != null && belowBlock.isSolid()) {
        PathPosition landingPosition = currentPosition.add(0, -i, 0);
        return new FilterOutcome(FilterResult.PASS, landingPosition);
      }
    }

    return new FilterOutcome(FilterResult.FAIL, currentPosition);
  }
}
