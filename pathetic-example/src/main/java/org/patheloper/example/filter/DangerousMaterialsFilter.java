package org.patheloper.example.filter;

import java.util.EnumSet;
import org.bukkit.Material;
import org.patheloper.api.pathing.filter.FilterOutcome;
import org.patheloper.api.pathing.filter.FilterResult;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A PathFilter that excludes nodes which are located on or near dangerous materials like lava.
 *
 * @api.Note Due to the radius check, this filter can be computationally expensive.
 */
public class DangerousMaterialsFilter implements PathFilter {

  private final EnumSet<Material> dangerousMaterials;
  private final int radius;

  /**
   * Constructor to initialize the filter with a list of dangerous materials and a radius to check
   * around each node.
   *
   * @param dangerousMaterials The list of materials to avoid, e.g., List.of(Material.LAVA,
   *     Material.CACTUS).
   * @param radius The radius around each node to check for the dangerous materials.
   */
  public DangerousMaterialsFilter(EnumSet<Material> dangerousMaterials, int radius) {
    this.dangerousMaterials = dangerousMaterials;
    this.radius = radius;
  }

  /**
   * Filters out nodes that are located on or near any of the specified dangerous materials.
   *
   * @param context The context of the current pathfinding validation.
   * @return a {@link FilterOutcome} indicating if the node is safe (PASS) or unsafe (FAIL).
   */
  @Override
  public FilterOutcome filter(PathValidationContext context) {
    PathPosition position = context.getTargetPosition();
    SnapshotManager snapshotManager = context.getSnapshotManager();

    // Check the surrounding blocks within the specified radius for dangerous materials
    for (int x = -radius; x <= radius; x++) {
      for (int y = -radius; y <= radius; y++) {
        for (int z = -radius; z <= radius; z++) {
          PathBlock block = snapshotManager.getBlock(position.add(x, y, z));
          if (block != null
              && dangerousMaterials.contains(block.getBlockInformation().getMaterial())) {
            // The node is near a dangerous material, so it's excluded
            return new FilterOutcome(FilterResult.FAIL, position); // No change to the target
          }
        }
      }
    }

    // The node is safe, return PASS with the current target
    return new FilterOutcome(FilterResult.PASS, position);
  }
}
