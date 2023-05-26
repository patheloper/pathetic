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

        boolean canStandOnIt = canStandOnBlock(position, snapshotManager, block, blockBelow);

        if (canStandOnIt) {
            this.lastExamined = position;
            return true;
        }

        if (lastExamined != null) {
            boolean withinDistance = isWithinDistance(lastExamined, position);
            boolean isHigher = isHigherPosition(lastExamined, position);
            boolean isLower = isLowerPosition(lastExamined, position);
            boolean isPassable = isPassableBlock(block, snapshotManager, areBlocksAbovePassable(position, snapshotManager));

            return withinDistance && (isHigher || isLower) && isPassable;
        }

        return false;
    }

    private boolean canStandOnBlock(PathPosition position, SnapshotManager snapshotManager, PathBlock block, PathBlock blockBelow) {
        boolean areBlocksAbovePassable = areBlocksAbovePassable(position, snapshotManager);

        for (int i = 1; i < height; i++) {
            PathBlock blockAbove = snapshotManager.getBlock(position.add(0, i, 0));
            if (!blockAbove.isPassable()) {
                areBlocksAbovePassable = false;
                break;
            }
        }

        return block.isPassable() && blockBelow.isSolid() && areBlocksAbovePassable;
    }

    private boolean isWithinDistance(PathPosition lastExamined, PathPosition position) {
        double distance = lastExamined.distance(position);
        return distance <= 3;
    }

    private boolean isHigherPosition(PathPosition lastExamined, PathPosition position) {
        return position.getY() - lastExamined.getY() > 2;
    }

    private boolean isLowerPosition(PathPosition lastExamined, PathPosition position) {
        return lastExamined.getY() - position.getY() > 2;
    }

    private boolean isPassableBlock(PathBlock block, SnapshotManager snapshotManager, boolean areBlocksAbovePassable) {
        return block.isPassable() && areBlocksAbovePassable;
    }

    private boolean areBlocksAbovePassable(PathPosition position, SnapshotManager snapshotManager) {
        boolean areBlocksAbovePassable = true;
        for (int i = 1; i < height; i++) {
            PathBlock blockAbove = snapshotManager.getBlock(position.add(0, i, 0));
            if (!blockAbove.isPassable()) {
                areBlocksAbovePassable = false;
                break;
            }
        }
        return areBlocksAbovePassable;
    }
}
