package org.patheloper.example.filter;

import java.util.EnumSet;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.patheloper.api.pathing.filter.FilterOutcome;
import org.patheloper.api.pathing.filter.FilterResult;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;

/** A PathFilter that excludes nodes located on harmful materials like fire or magma. */
public class NoHarmFilter implements PathFilter {

  private final EnumSet<Material> harmfulMaterials;

  /**
   * Constructor to initialize the filter with harmful materials to check.
   *
   * @param harmfulMaterials The list of harmful materials to avoid (e.g., fire, magma).
   */
  public NoHarmFilter(EnumSet<Material> harmfulMaterials) {
    this.harmfulMaterials = harmfulMaterials;
  }

  @Override
  public FilterOutcome filter(@NonNull PathValidationContext pathValidationContext) {
    SnapshotManager snapshotManager = pathValidationContext.getSnapshotManager();
    PathBlock currentBlock = snapshotManager.getBlock(pathValidationContext.getTargetPosition());
    PathBlock blockBelow =
        snapshotManager.getBlock(pathValidationContext.getTargetPosition().add(0, -1, 0));

    if (currentBlock != null
        && harmfulMaterials.contains(currentBlock.getBlockInformation().getMaterial())) {
      return new FilterOutcome(FilterResult.WARNING, pathValidationContext.getTargetPosition());
    }

    if (blockBelow != null
        && harmfulMaterials.contains(blockBelow.getBlockInformation().getMaterial())) {
      return new FilterOutcome(FilterResult.WARNING, pathValidationContext.getTargetPosition());
    }

    return new FilterOutcome(FilterResult.PASS, pathValidationContext.getTargetPosition());
  }
}
