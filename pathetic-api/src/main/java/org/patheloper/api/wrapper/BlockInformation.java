package org.patheloper.api.wrapper;

import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

/**
 * Represents information about a block in the world, independent of Bukkit.
 */
@Value
public class BlockInformation {

  /** The material of the represented block */
  @NonNull Material material;

  /**
   * The block state of the represented block -- GETTER -- Gets the block state of the represented
   * block
   *
   * @api.Note This is only available in v. 1.13 or above and therefore nullable
   */
  @Nullable BlockState blockState;
}
