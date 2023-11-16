package org.patheloper.api.pathing.strategy;

import lombok.NonNull;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link Pathfinder}.
 */
@FunctionalInterface
public interface PathfinderStrategy {
    
    /**
     * Returns whether the given {@link PathPosition} is valid.
     *
     * @param position        The {@link PathPosition} to check
     * @param snapshotManager The current {@link SnapshotManager} for getting blocks
     */
    boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager);

}
