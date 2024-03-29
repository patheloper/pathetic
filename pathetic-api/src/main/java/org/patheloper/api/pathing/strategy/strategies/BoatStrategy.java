package org.patheloper.api.pathing.strategy.strategies;

import org.bukkit.Material;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

public class BoatStrategy implements PathfinderStrategy {

  @Override
  public boolean isValid(PathValidationContext pathValidationContext) {
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();
    PathPosition pathPosition = pathValidationContext.getPosition();

    return snapshotManager
                .getBlock(pathPosition.subtract(0, 1, 0))
                .getBlockInformation()
                .getMaterial()
            == Material.WATER
        && snapshotManager.getBlock(pathPosition).isPassable();
  }
}
