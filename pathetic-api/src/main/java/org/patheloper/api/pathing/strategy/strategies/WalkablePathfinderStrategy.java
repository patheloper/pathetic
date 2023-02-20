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

    private PathPosition lastExamined;

    public WalkablePathfinderStrategy() {
        this(2);
    }

    public WalkablePathfinderStrategy(int height) {
        this.height = height;
    }

    @Override
    public boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager) {

        PathBlock block = snapshotManager.getBlock(position);
        PathBlock blockBelow = snapshotManager.getBlock(position.add(0, -1, 0));

        boolean areBlocksAbovePassable = true;
        for (int i = 1; i < height; i++) {
            PathBlock blockAbove = snapshotManager.getBlock(position.add(0, i, 0));
            if (!blockAbove.isPassable()) {
                areBlocksAbovePassable = false;
                break;
            }
        }

        boolean canStandOnIt = block.isPassable() && blockBelow.isSolid() && areBlocksAbovePassable;

        if(canStandOnIt) {
            this.lastExamined = position;
            return true;
        }

        if(lastExamined != null) {

            double distance = lastExamined.distance(position);
            boolean withinDistance = distance <= 3;

            boolean isHigher = position.getY() - lastExamined.getY() > 2;
            boolean isLower = lastExamined.getY() - position.getY() > 2;
            boolean isPassable = block.isPassable() && areBlocksAbovePassable;

            return withinDistance && (isHigher || isLower) && isPassable;
        }

        return false;
    }
}
