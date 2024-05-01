package org.patheloper.api.pathing.strategy.strategies;

import org.bukkit.Material;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

public class PlayerWalkableStrategy extends WalkablePathfinderStrategy {

  @Override
  public boolean isValid(PathValidationContext pathValidationContext) {
    PathPosition pathPosition = pathValidationContext.getPosition();
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();

    if (snapshotManager.getBlock(pathPosition).isPassable()
        && canStandOn(snapshotManager.getBlock(pathPosition), snapshotManager)) {
      return true;
    }

    if (snapshotManager.getBlock(pathPosition.subtract(0, 1, 0)).getBlockInformation().getMaterial()
            == Material.WATER
        && snapshotManager.getBlock(pathPosition).isPassable()) return true;

    return snapshotManager.getBlock(pathPosition).getBlockInformation().getMaterial()
        == Material.WATER;
  }
}
