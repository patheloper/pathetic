package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

import javax.annotation.Nullable;

@Value
@ToString
@EqualsAndHashCode
public class BlockInformation {

  /** The material of the represented block */
  @NonNull
  Material material;

  /**
   * The block state of the represented block -- GETTER -- Gets the block state of the represented block
   *
   * @api.Note This is only available in v. 1.13 or above and therefore nullable
   */
  @Nullable
  BlockState blockState;
}
