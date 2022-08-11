package xyz.ollieee.api.pathing.strategy;

import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.Objects;

/**
 * An essentials bundle for {@link PathfinderStrategy}'s.
 */
public final class StrategyData {

    private final SnapshotManager snapshotManager;
    private final PathLocation pathLocation;

    public StrategyData(SnapshotManager snapshotManager, PathLocation pathLocation) {
        this.snapshotManager = snapshotManager;
        this.pathLocation = pathLocation;
    }

    public SnapshotManager getSnapshotManager() {
        return this.snapshotManager;
    }

    public PathLocation getPathLocation() {
        return this.pathLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyData that = (StrategyData) o;
        return Objects.equals(snapshotManager, that.snapshotManager) && Objects.equals(pathLocation, that.pathLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snapshotManager, pathLocation);
    }

    public String toString() {
        return "StrategyData(snapshotManager=" + this.getSnapshotManager() + ", pathLocation=" + this.getPathLocation() + ")";
    }
}
