package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public class ParkourPathfinderStrategy implements PathfinderStrategy {

    private PathLocation lastStandable = null;

    @Override
    public boolean isValid(@NonNull PathLocation location, @NonNull SnapshotManager snapshotManager) {

        PathBlock block = snapshotManager.getBlock(location);
        boolean canStand = snapshotManager.getBlock(location.clone().add(0, -1, 0)).isSolid();

        if (block.isPassable()
                && snapshotManager.getBlock(location.clone().add(0, 1, 0)).isPassable()
                && canStand) {
            lastStandable = location;
            return true;
        }

        if (lastStandable != null && lastStandable.distance(location) <= 3) {

            PathLocation jumpLocation = location.clone().add(0, 1, 0);

            if(jumpLocation.getY() - lastStandable.getY() > 2)
                return false;

            PathBlock jumpBlock = snapshotManager.getBlock(jumpLocation);
            PathBlock jumpBlockAbove = snapshotManager.getBlock(jumpLocation.add(0, 1, 0));

            return jumpBlock.isPassable() && jumpBlockAbove.isPassable();
        }

        return false;
    }

    @Override
    public void cleanup() {
        lastStandable = null;
    }
}
