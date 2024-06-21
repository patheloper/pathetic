package org.patheloper.api.pathing.filter.filters;

import lombok.NonNull;
import org.bukkit.Material;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/** A PathFilter implementation that determines if a path is through water. */
public class WaterPathFilter implements PathFilter {

  @Override
  public boolean filter(@NonNull PathValidationContext pathValidationContext) {
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();
    PathPosition pathPosition = pathValidationContext.getPosition();

    return snapshotManager.getBlock(pathPosition).getBlockInformation().getMaterial()
        == Material.WATER;
  }
}
