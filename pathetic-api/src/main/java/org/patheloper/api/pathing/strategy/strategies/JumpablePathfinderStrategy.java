package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.annotation.Experimental;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.terrain.TerrainProvider;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A {@link WalkablePathfinderStrategy} that allows jumping.
 *
 * @experimental This class is experimental and may be change a lot in the future
 */
@Experimental
public class JumpablePathfinderStrategy extends WalkablePathfinderStrategy {

  private final int jumpHeight;
  private final int maxJumpDistance;

  private PathPosition lastValidPosition = null;

  public JumpablePathfinderStrategy() {
    this(2, 1, 4);
  }

  public JumpablePathfinderStrategy(int height, int jumpHeight, int maxJumpDistance) {
    super(height);

    if (jumpHeight <= 0) throw new IllegalArgumentException("Jump height must be greater than 0");
    if (maxJumpDistance <= 0)
      throw new IllegalArgumentException("Jump distance must be greater than 0");

    this.jumpHeight = jumpHeight;
    this.maxJumpDistance = maxJumpDistance;
  }

  @Override
  public boolean isValid(@NonNull PathValidationContext pathValidationContext) {
    PathPosition position = pathValidationContext.getPosition();
    TerrainProvider terrainProvider = pathValidationContext.getTerrainProvider();

    if (lastValidPosition == null) lastValidPosition = position;

    PathBlock startBlock = terrainProvider.getBlock(position);
    if (canStandOn(startBlock, terrainProvider)) {
      lastValidPosition = position;
      return true;
    }

    if ((position.getBlockY() - lastValidPosition.getBlockY()) > jumpHeight) return false;

    return startBlock.isPassable() && position.distance(lastValidPosition) <= maxJumpDistance;
  }
}
