package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * A {@link PathfinderStrategy} to find the best walkable or even jumpable path.
 *
 * @deprecated WIP
 */
@Deprecated
public class ParkourPathfinderStrategy implements PathfinderStrategy {

    private PathLocation lastStandable = null;

    @Override
    public boolean isValid(@NonNull PathLocation location, @NonNull SnapshotManager snapshotManager) {

        PathBlock block = snapshotManager.getBlock(location);

        if(canTwoBlockHighEntityStandOnIt(block, snapshotManager)) {
            this.lastStandable = location;
            return true;
        }

        if (identifiesAsJump(block)) {
            PathBlock blockAbove = snapshotManager.getBlock(location.add(0, 1, 0));
            return block.isPassable() && blockAbove.isPassable();
        }

        return false;
    }

    @Override
    public void cleanup() {
        lastStandable = null;
    }

    private boolean canTwoBlockHighEntityStandOnIt(PathBlock block, SnapshotManager snapshotManager) {

        PathLocation location = block.getPathLocation();

        PathBlock blockAbove = snapshotManager.getBlock(location.add(0, 1, 0));
        PathBlock blockBelow = snapshotManager.getBlock(location.add(0, -1, 0));

        return block.isPassable() && blockAbove.isPassable() && blockBelow.isSolid();
    }

    private boolean identifiesAsJump(PathBlock block) {
        PathLocation location = block.getPathLocation();
        return lastStandable != null && lastStandable.distance(location) <= 3 && location.getY() - lastStandable.getY() > 2;
    }
}
