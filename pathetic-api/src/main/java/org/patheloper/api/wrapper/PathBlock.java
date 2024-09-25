package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/** Represents a block in the world, independent of Bukkit. */
@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class PathBlock {

  private final PathPosition pathPosition;
  private final BlockInformation blockInformation;

  /**
   * Checks if the block is air.
   *
   * @return {@code true} if the block is air, {@code false} otherwise
   */
  public boolean isAir() {
    return blockInformation.getMaterial().isAir();
  }

  /**
   * Checks if the block is passable (i.e., not solid).
   *
   * @return {@code true} if the block is passable, {@code false} if it's solid
   */
  public boolean isPassable() {
    return !isSolid();
  }

  /**
   * Checks if the block is solid.
   *
   * @return {@code true} if the block is solid, {@code false} if it's passable
   */
  public boolean isSolid() {
    return blockInformation.getMaterial().isSolid();
  }

  /**
   * Gets the X coordinate of the block's position.
   *
   * @return the X coordinate of the block
   */
  public int getBlockX() {
    return this.pathPosition.getBlockX();
  }

  /**
   * Gets the Y coordinate of the block's position.
   *
   * @return the Y coordinate of the block
   */
  public int getBlockY() {
    return this.pathPosition.getBlockY();
  }

  /**
   * Gets the Z coordinate of the block's position.
   *
   * @return the Z coordinate of the block
   */
  public int getBlockZ() {
    return this.pathPosition.getBlockZ();
  }
}
