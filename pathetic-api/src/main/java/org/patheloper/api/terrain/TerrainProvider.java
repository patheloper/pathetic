package org.patheloper.api.terrain;

import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;

/**
 * The TerrainProvider interface defines methods for retrieving block data at specific
 * positions within a world.
 */
public interface TerrainProvider {

  /**
   * Gets the block at the given position
   *
   * @api.Note If the pathfinder is not permitted to load chunks, this method will return null if
   *     the chunk is not loaded.
   * @param position the position to get as block-form
   * @return {@link PathBlock} the block.
   */
  PathBlock getBlock(PathPosition position);
}
