package xyz.ollieee.api.pathing.strategy;

import lombok.NonNull;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link xyz.ollieee.api.pathing.Pathfinder}
 */
@FunctionalInterface
public interface PathfinderStrategy {
    
    /**
     * Implement the logic to see if a given location is valid for a strategy
     * <p>
     *
     * @param location        The {@link PathLocation} to check
     * @param snapshotManager The current {@link SnapshotManager} for getting blocks
     */
    boolean isValid(@NonNull PathLocation location, @NonNull SnapshotManager snapshotManager);

    /**
     * Called when the {@link xyz.ollieee.api.pathing.Pathfinder} is done with pathfinding.
     * <p>
     * This is useful for resetting variables that are used in the {@link #isValid(PathLocation, SnapshotManager)} method
     * since the {@link xyz.ollieee.api.pathing.Pathfinder} will reuse the same instance of the strategy.
     */
    default void cleanup() {}
}
