package xyz.oli.pathing;

import xyz.oli.wrapper.PathBlock;
import xyz.oli.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     * @param location the location to check
     * @return PathBlock the block
     */
    PathBlock getBlock(PathLocation location);
}
