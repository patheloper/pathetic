package xyz.ollieee.api.pathing.strategy.strategies;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public class WalkablePathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(PathLocation location, SnapshotManager snapshotManager) {

        PathBlock block = snapshotManager.getBlock(location);
        boolean canStand = snapshotManager.getBlock(location.clone().add(0, -1, 0)).isSolid();

        return block.isPassable() && snapshotManager.getBlock(location.clone().add(0, 1, 0)).isPassable() && canStand;
    }

}
