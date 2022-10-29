package xyz.ollieee.api.snapshot;

import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     *
     * @param location the location to check
     * @return {@link PathBlock} the block. Null if it cannot be loaded
     */
    PathBlock getBlock(PathLocation location);
}
