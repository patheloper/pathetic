package org.patheloper.api.pathing.strategy;

import lombok.NonNull;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathLocation;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link Pathfinder}
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
     * Called when the {@link Pathfinder} is done with pathfinding.
     * <p>
     * This is useful for resetting variables that are used in the {@link #isValid(PathLocation, SnapshotManager)} method
     * since the {@link Pathfinder} will reuse the same instance of the strategy.
     */
    default void cleanup() {}
}
