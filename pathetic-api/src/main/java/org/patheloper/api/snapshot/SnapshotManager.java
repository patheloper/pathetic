package org.patheloper.api.snapshot;

import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

public interface SnapshotManager {

    /**
     * Gets the block at the given position
     *
     * @param position the position to get as block-form
     * @return {@link PathBlock} the block.
     */
    PathBlock getBlock(PathPosition position);
}
