package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.annotation.Experimental;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A {@link PathfinderStrategy} to find the best path for a player. This currently only takes basic jumps and walkable paths into account.
 *
 * @experimental This strategy is experimental and may not work as expected.
 */
@Experimental
public class PlayerPathfinderStrategy implements PathfinderStrategy {

    private static final double MAX_JUMP_HEIGHT_IN_BLOCKS = 1.25;
    private static final int MAX_JUMP_DISTANCE_IN_BLOCKS = 4;
    private static final double GRAVITY = 0.08;

    private PathPosition lastStandOn = null;

    @Override
    public boolean isValid(@NonNull PathPosition location, @NonNull SnapshotManager snapshotManager) {

        if(lastStandOn == null) // First iteration, literally the spawn point
            lastStandOn = location;

        if(!isGenerallyPassable(location, snapshotManager))
            return false;

        if(canStandOn(location, snapshotManager)) {
            lastStandOn = location;
            return true;
        }

        if(canJumpFrom(lastStandOn, snapshotManager) && canJumpTo(location, snapshotManager))
            return true;

        if (canJumpFrom(lastStandOn, snapshotManager) && canJumpOver(location, snapshotManager)) {
            double distance = lastStandOn.distance(location);
            double height = location.getY() - lastStandOn.getY();
            double maxDistance = calculateMaxDistance(height);

            return distance <= MAX_JUMP_DISTANCE_IN_BLOCKS && height <= MAX_JUMP_HEIGHT_IN_BLOCKS && distance <= maxDistance;
        }

        return false;
    }

    private double calculateMaxDistance(double height) {
        double initialVelocity = Math.sqrt(2 * GRAVITY * height);
        double timeToReachMaxHeight = initialVelocity / GRAVITY;

        double maxHorizontalDistance = initialVelocity * timeToReachMaxHeight;
        double maxHeight = initialVelocity * timeToReachMaxHeight - 0.5 * GRAVITY * timeToReachMaxHeight * timeToReachMaxHeight;

        return Math.sqrt(maxHorizontalDistance * maxHorizontalDistance + maxHeight * maxHeight);
    }

    private boolean isGenerallyPassable(PathPosition pathPosition, SnapshotManager snapshotManager) {
        return snapshotManager.getBlock(pathPosition).isPassable()
                && snapshotManager.getBlock(pathPosition.add(0, 1, 0)).isPassable();
    }

    private boolean canStandOn(PathPosition pathPosition, SnapshotManager snapshotManager) {
        return isGenerallyPassable(pathPosition, snapshotManager) && snapshotManager.getBlock(pathPosition.add(0, -1, 0)).isSolid();
    }

    private boolean canJumpFrom(PathPosition pathPosition, SnapshotManager snapshotManager) {
        return canStandOn(pathPosition, snapshotManager) && snapshotManager.getBlock(pathPosition.add(0, 2, 0)).isPassable();
    }

    private boolean canJumpTo(PathPosition pathPosition, SnapshotManager snapshotManager) {
        return canStandOn(pathPosition, snapshotManager);
    }

    private boolean canJumpOver(PathPosition pathPosition, SnapshotManager snapshotManager) {
        return isGenerallyPassable(pathPosition, snapshotManager) && snapshotManager.getBlock(pathPosition.add(0, 2, 0)).isPassable();
    }
}
