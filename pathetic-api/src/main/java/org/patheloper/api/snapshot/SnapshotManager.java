package org.patheloper.api.snapshot;

import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at the given location
     *
     * @param location the location to get as block-form
     * @return {@link PathBlock} the block.
     */
    PathBlock getBlock(PathLocation location);
}
