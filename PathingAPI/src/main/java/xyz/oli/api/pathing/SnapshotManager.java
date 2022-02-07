package xyz.oli.api.pathing;

import xyz.oli.api.wrapper.PathBlock;
import xyz.oli.api.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     * @param location the location to check
     * @return PathBlock the block
     */
    PathBlock getBlock(PathLocation location);
}
