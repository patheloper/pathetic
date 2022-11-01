package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public class ParkourPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull SnapshotManager snapshotManager, @NonNull PathLocation pathLocation) {

        PathBlock pathBlock = snapshotManager.getBlock(pathLocation);

        return pathBlock.isPassable()
                && snapshotManager.getBlock(pathLocation.clone().add(0, 1, 0)).isPassable()
                && snapshotManager.getBlock(pathLocation.clone().add(0, 2, 0)).isPassable();
    }
}
