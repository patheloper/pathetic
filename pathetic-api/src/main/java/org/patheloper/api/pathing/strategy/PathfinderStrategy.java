package org.patheloper.api.pathing.strategy;

import lombok.NonNull;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link Pathfinder}.
 * <p>
 *
 * @api.Note The instance of the strategy is created once every pathing and then thrown away.
 *          It is not recommended to store any state in the strategy.
 */
@FunctionalInterface
public interface PathfinderStrategy {
    
    /**
     * Implement the logic to see if a given position is valid for a strategy
     * <p>
     *
     * @param position        The {@link PathPosition} to check
     * @param snapshotManager The current {@link SnapshotManager} for getting blocks
     */
    boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager);

}
