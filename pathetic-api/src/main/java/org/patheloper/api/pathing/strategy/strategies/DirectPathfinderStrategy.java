package org.patheloper.api.pathing.strategy.strategies;

import lombok.NonNull;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathLocation;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;

/**
 * A {@link PathfinderStrategy} to find the direct path to a given endpoint
 */
public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathLocation location, @NonNull SnapshotManager snapshotManager) {
        return snapshotManager.getBlock(location).isPassable();
    }
}
