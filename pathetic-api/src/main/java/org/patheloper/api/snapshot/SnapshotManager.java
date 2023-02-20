package org.patheloper.api.snapshot;

import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

public interface SnapshotManager {

    /**
     * Gets the block at the given position
     *
     * @apiNote If the pathfinder is not permitted to load chunks, this method will return null if the chunk is not loaded.
     *
     * @param position the position to get as block-form
     * @return {@link PathBlock} the block.
     */
    PathBlock getBlock(PathPosition position);
}
