package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * A {@link PathfinderStrategy} to find a walkable path to a given endpoint
 *
 * @deprecated Use WIP
 */
@Deprecated
public class WalkablePathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull SnapshotManager snapshotManager, @NonNull PathLocation pathLocation) {

        PathBlock pathBlock = snapshotManager.getBlock(pathLocation);

        PathLocation below = pathBlock.getPathLocation().clone().subtract(0, 1, 0);
        PathLocation above = pathBlock.getPathLocation().clone().add(0, 1, 0);
        PathLocation aboveAbove = above.clone().add(0, 1, 0);

        return pathBlock.isPassable()
                && snapshotManager.getBlock(below).getPathBlockType() == PathBlockType.SOLID
                && snapshotManager.getBlock(above).isPassable()
                && snapshotManager.getBlock(aboveAbove).isPassable();
    }
}
