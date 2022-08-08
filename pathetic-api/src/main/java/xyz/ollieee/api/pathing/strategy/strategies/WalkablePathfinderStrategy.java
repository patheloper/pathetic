package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.StrategyData;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

@Deprecated
public class WalkablePathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull StrategyData essentials) {

        SnapshotManager snapshotManager = essentials.getSnapshotManager();
        PathBlock pathBlock = snapshotManager.getBlock(essentials.getPathLocation());

        PathLocation below = pathBlock.getPathLocation().clone().subtract(0, 1, 0);
        PathLocation above = pathBlock.getPathLocation().clone().add(0, 1, 0);
        PathLocation aboveAbove = above.clone().add(0, 1, 0);

        return pathBlock.isPassable()
                && snapshotManager.getBlock(below).getPathBlockType() == PathBlockType.SOLID
                && snapshotManager.getBlock(above).isPassable()
                && snapshotManager.getBlock(aboveAbove).isPassable();
    }
}
