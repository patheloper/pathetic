package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public class ParkourPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathLocation location, @NonNull SnapshotManager snapshotManager) {

        PathBlock pathBlock = snapshotManager.getBlock(location);

        return snapshotManager.getBlock(location.add(0, -1, 0)).isSolid() &&
                pathBlock.isPassable()
                && snapshotManager.getBlock(location.clone().add(0, 1, 0)).isPassable()
                && snapshotManager.getBlock(location.clone().add(0, 2, 0)).isPassable(); // More or less optional, but for parkour definitely needed
    }
}
