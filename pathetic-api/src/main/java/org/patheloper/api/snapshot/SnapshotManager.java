package org.patheloper.api.snapshot;

import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/**
 * The SnapshotManager interface defines methods for retrieving block data snapshots at specific positions within a
 * Minecraft world.
 */
public interface SnapshotManager {

  /**
   * Gets the block at the given position
   *
   * @param position the position to get as block-form
   *
   * @return {@link PathBlock} the block.
   *
   * @api.Note If the pathfinder is not permitted to load chunks, this method will return null if the chunk is not
   * loaded.
   */
  PathBlock getBlock(PathPosition position);
}
