package xyz.ollieee.api.pathing.world.chunk;

import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     * @param location the location to check
     * @return PathBlock the block
     */
    PathBlock getBlock(PathLocation location);
}
