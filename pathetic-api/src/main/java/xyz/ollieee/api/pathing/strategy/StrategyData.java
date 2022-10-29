package xyz.ollieee.api.pathing.strategy;

import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.Objects;
import java.util.UUID;

/**
 * An essentials bundle for {@link PathfinderStrategy}'s.
 */
public final class StrategyData implements SnapshotManager {

    private final SnapshotManager snapshotManager;
    private final PathLocation pathLocation;
    private final Boolean loadChunks;

    public StrategyData(SnapshotManager snapshotManager, PathLocation pathLocation, Boolean loadChunks) {
        this.snapshotManager = snapshotManager;
        this.pathLocation = pathLocation;
        this.loadChunks = loadChunks;
    }

    /**
     * Gets the {@link SnapshotManager} for this strategy data
     *
     * @return The {@link SnapshotManager}
     * @deprecated Use the {@link StrategyData} as a {@link SnapshotManager} instead
     */
    @Deprecated
    public SnapshotManager getSnapshotManager() {
        return this;
    }

    /**
     * Gets the {@link PathLocation} for this strategy data
     *
     * @return The {@link PathLocation}
     */
    public PathLocation getPathLocation() {
        return this.pathLocation;
    }

    @Override
    public PathBlock getBlock(PathLocation location) {
        return this.snapshotManager.getBlock(location, this.loadChunks);
    }

    /**
     * Gets the block at a location
     *
     * @param location   the location to check
     * @param loadChunks Ignored in this implementation
     * @return
     */
    @Override
    public PathBlock getBlock(PathLocation location, Boolean loadChunks) {
        return this.snapshotManager.getBlock(location, this.loadChunks);
    }

    @Override
    public boolean invalidateChunk(UUID worldUUID, int chunkX, int chunkZ) {
        return this.snapshotManager.invalidateChunk(worldUUID, chunkX, chunkZ);
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
