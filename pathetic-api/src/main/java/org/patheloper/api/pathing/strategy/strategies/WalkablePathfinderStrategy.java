package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathLocation;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;

/**
 * A {@link PathfinderStrategy} to find the best walkable path.
 */
public class WalkablePathfinderStrategy implements PathfinderStrategy {

    private final int height;

    private PathLocation lastExamined;

    public WalkablePathfinderStrategy() {
        this(2);
    }

    public WalkablePathfinderStrategy(int height) {
        this.height = height;
    }

    @Override
    public boolean isValid(@NonNull PathLocation location, @NonNull SnapshotManager snapshotManager) {

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

        boolean canStandOnIt = block.isPassable() && blockBelow.isSolid() && areBlocksAbovePassable;

        if(canStandOnIt) {
            this.lastExamined = location;
            return true;
        }

        if(lastExamined != null) {

            double distance = lastExamined.distance(location);
            boolean withinDistance = distance <= 3;

            boolean isHigher = location.getY() - lastExamined.getY() > 2;
            boolean isLower = lastExamined.getY() - location.getY() > 2;
            boolean isPassable = block.isPassable() && areBlocksAbovePassable;

            return withinDistance && (isHigher || isLower) && isPassable;
        }

        return false;
    }
}
