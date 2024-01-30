package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.terrain.TerrainProvider;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/** A {@link PathfinderStrategy} to find the best walkable path. */
public class WalkablePathfinderStrategy implements PathfinderStrategy {

  private final int height;

  public WalkablePathfinderStrategy() {
    this(2);
  }

  public WalkablePathfinderStrategy(int height) {
    if (height <= 0) throw new IllegalArgumentException("Height must be greater than 0");

    this.height = height;
  }

  @Override
  public boolean isValid(@NonNull PathValidationContext pathValidationContext) {
    TerrainProvider terrainProvider = pathValidationContext.getTerrainProvider();
    PathBlock block = terrainProvider.getBlock(pathValidationContext.getPosition());
    return canStandOn(block, terrainProvider);
  }

  protected boolean canStandOn(PathBlock block, TerrainProvider terrainProvider) {
    PathBlock below = terrainProvider.getBlock(block.getPathPosition().add(0, -1, 0));
    return below.isSolid() && areBlocksAbovePassable(block.getPathPosition(), terrainProvider);
  }

  protected boolean areBlocksAbovePassable(PathPosition position, TerrainProvider terrainProvider) {
    for (int i = 0; i < height; i++) {
      PathBlock block = terrainProvider.getBlock(position.add(0, i, 0));
      if (!block.isPassable()) {
        return false;
      }
    }
    return true;
  }
}
