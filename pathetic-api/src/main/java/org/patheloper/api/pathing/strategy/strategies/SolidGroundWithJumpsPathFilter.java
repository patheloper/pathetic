package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.annotation.Experimental;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A {@link SolidGroundPathFilter} that allows jumping.
 *
 * @experimental This class is experimental and may be change a lot in the future
 */
@Experimental
public class SolidGroundWithJumpsPathFilter extends SolidGroundPathFilter {

  private final int jumpHeight;
  private final int maxJumpDistance;

  private PathPosition lastValidPosition = null;

  public SolidGroundWithJumpsPathFilter() {
    this(2, 1, 4);
  }

  public SolidGroundWithJumpsPathFilter(int height, int jumpHeight, int maxJumpDistance) {
    super(height);

    if (jumpHeight <= 0) throw new IllegalArgumentException("Jump height must be greater than 0");
    if (maxJumpDistance <= 0)
      throw new IllegalArgumentException("Jump distance must be greater than 0");

    this.jumpHeight = jumpHeight;
    this.maxJumpDistance = maxJumpDistance;
  }

  @Override
  public boolean filter(@NonNull PathValidationContext pathValidationContext) {
    PathPosition position = pathValidationContext.getPosition();
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();

    if (lastValidPosition == null) lastValidPosition = position;

    PathBlock startBlock = snapshotManager.getBlock(position);
    if (canStandOn(startBlock, snapshotManager)) {
      lastValidPosition = position;
      return true;
    }

    int heightDiff = position.getBlockY() - lastValidPosition.getBlockY();
    if (isJumpingDown(position, lastValidPosition)) {
      heightDiff *= -1;
    }

    if (heightDiff > jumpHeight) {
      return false;
    }

    return startBlock.isPassable() && position.distance(lastValidPosition) <= maxJumpDistance;
  }

  private boolean isJumpingDown(PathPosition position, PathPosition lastValidPosition) {
    return position.getBlockX() == lastValidPosition.getBlockX()
        && position.getBlockZ() == lastValidPosition.getBlockZ()
        && position.getBlockY() < lastValidPosition.getBlockY();
  }
}
