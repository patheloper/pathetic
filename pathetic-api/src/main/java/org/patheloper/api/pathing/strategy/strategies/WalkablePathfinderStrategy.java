package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;

/**
 * A {@link PathfinderStrategy} to find the best walkable path.
 */
public class WalkablePathfinderStrategy implements PathfinderStrategy {

    private final int height;

    public WalkablePathfinderStrategy() {
        this(2);
    }

    public WalkablePathfinderStrategy(int height) {
        this.height = height;
    }

    @Override
    public boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager) {
        PathBlock block = snapshotManager.getBlock(position);
        return canStandOn(block, snapshotManager);
    }
    
    protected boolean canStandOn(PathBlock block, SnapshotManager snapshotManager) {
        PathBlock below = snapshotManager.getBlock(block.getPathPosition().add(0, -1, 0));
        return areBlocksAbovePassable(block.getPathPosition(), snapshotManager)
                && block.isPassable()
                && below.isSolid();
    }

    protected boolean areBlocksAbovePassable(PathPosition position, SnapshotManager snapshotManager) {
        boolean areBlocksAbovePassable = true;
        for(int i = 1; i <= height; i++) {
            PathBlock block = snapshotManager.getBlock(position.add(0, i, 0));
            if(!block.isPassable()) {
                areBlocksAbovePassable = false;
                break;
            }
        }
        return areBlocksAbovePassable;
    }
}
