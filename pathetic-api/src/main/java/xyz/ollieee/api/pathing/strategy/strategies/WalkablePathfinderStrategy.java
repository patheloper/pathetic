package xyz.ollieee.api.pathing.strategy.strategies;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * A {@link PathfinderStrategy} to find the best walkable path.
 */
public class WalkablePathfinderStrategy implements PathfinderStrategy {

    private final int height;

    /*
     * TODO: Add going up or down (e.g. stairs, jumping down, jumping up)
     */
    private PathLocation lastExamined;

    public WalkablePathfinderStrategy() {
        this(2);
    }

    public WalkablePathfinderStrategy(int height) {
        this.height = height;
    }

    @Override
    public boolean isValid(PathLocation location, SnapshotManager snapshotManager) {

        PathBlock block = snapshotManager.getBlock(location);
        PathBlock blockBelow = snapshotManager.getBlock(location.add(0, -1, 0));

        boolean areBlocksAbovePassable = true;
        for (int i = 1; i < height; i++) {
            PathBlock blockAbove = snapshotManager.getBlock(location.add(0, i, 0));
            if (!blockAbove.isPassable()) {
                areBlocksAbovePassable = false;
                break;
            }
        }

        return block.isPassable() && areBlocksAbovePassable && blockBelow.isSolid();
    }

    @Override
    public void cleanup() {
        lastExamined = null;
    }
}
