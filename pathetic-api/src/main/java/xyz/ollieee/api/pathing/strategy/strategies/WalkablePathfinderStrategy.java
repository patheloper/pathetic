package xyz.ollieee.api.pathing.strategy.strategies;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public class WalkablePathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(PathLocation location, SnapshotManager snapshotManager) {

        PathBlock block = snapshotManager.getBlock(location);
        PathBlock blockAbove = snapshotManager.getBlock(location.add(0, 1, 0));
        PathBlock blockBelow = snapshotManager.getBlock(location.add(0, -1, 0));

        return block.isPassable() && blockAbove.isPassable() && blockBelow.isSolid();
    }

}
