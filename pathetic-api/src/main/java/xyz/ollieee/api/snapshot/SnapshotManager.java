package xyz.ollieee.api.snapshot;

import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at the given location
     *
     * @param location the location to get as block-form
     * @return {@link PathBlock} the block.
     */
    PathBlock getBlock(PathLocation location);
}
