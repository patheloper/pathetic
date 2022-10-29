package xyz.ollieee.api.snapshot;

import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.UUID;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     *
     * @param location the location to check
     * @return {@link PathBlock} the block. Null if it cannot be loaded
     */
    PathBlock getBlock(PathLocation location);

    /**
     * Deletes a snapshot from the cache
     *
     * @param worldUUID The world UUID
     * @param chunkX    The chunks X's coordinate
     * @param chunkZ    The chunks Z's coordinate
     * @return true If a snapshot was deleted, false if it wasn't
     */
    boolean invalidateChunk(UUID worldUUID, int chunkX, int chunkZ);
}
