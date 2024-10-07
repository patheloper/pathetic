package org.patheloper.api.pathing.filter.filters;

import lombok.NonNull;
import org.bukkit.Material;
import org.patheloper.api.pathing.filter.FilterOutcome;
import org.patheloper.api.pathing.filter.FilterResult;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/** A PathFilter implementation that determines if a path is through water. */
public class WaterPathFilter implements PathFilter {

  @Override
  public FilterOutcome filter(@NonNull PathValidationContext pathValidationContext) {
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();
    PathPosition pathPosition = pathValidationContext.getTargetPosition();

    if (snapshotManager.getBlock(pathPosition).getBlockInformation().getMaterial()
        == Material.WATER) {
      return new FilterOutcome(FilterResult.PASS, pathPosition);
    } else {
      return new FilterOutcome(FilterResult.FAIL, pathPosition);
    }
  }
}
