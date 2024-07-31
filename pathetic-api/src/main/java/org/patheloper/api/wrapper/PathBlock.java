package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/** A Class to represent a block in the world, except exempt of Bukkit */
@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class PathBlock {

  private final PathPosition pathPosition;
  private final BlockInformation blockInformation;

  /**
   * @return Whether the block is air
   */
  public boolean isAir() {










    return blockInformation.getMaterial().isAir();
  }

  /**
   * @return Whether the block is possible to walk through
   */
  public boolean isPassable() {










    return !isSolid();
  }

  /**
   * @return Whether the block is solid
   */
  public boolean isSolid() {










    return blockInformation.getMaterial().isSolid();
  }

  /**
   * Gets the X coordinate of the block
   *
   * @return The X coordinate of the block
   */
  public int getBlockX() {










    return this.pathPosition.getBlockX();
  }

  /**
   * Gets the Y coordinate of the block
   *
   * @return The Y coordinate of the block
   */
  public int getBlockY() {










    return this.pathPosition.getBlockY();
  }

  /**
   * Gets the Z coordinate of the block
   *
   * @return The Z coordinate of the block
   */
  public int getBlockZ() {










    return this.pathPosition.getBlockZ();
  }
}
