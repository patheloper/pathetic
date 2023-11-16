package org.patheloper.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A Class to represent a block in the world, except exempt of Bukkit
 */
@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class PathBlock {

    private final PathPosition pathPosition;
    private final PathBlockType pathBlockType;
    private final String blockName;

    /**
     * @return Whether the block is air
     */
    public boolean isAir() {
        return this.pathBlockType == PathBlockType.AIR;
    }

    /**
     * @return Whether the block is possible to walk through
     */
    public boolean isPassable() {
        return isAir() || this.pathBlockType == PathBlockType.OTHER;
    }
    
    /**
     * @return Whether the block is solid
     */
    public boolean isSolid() {
        return this.pathBlockType == PathBlockType.SOLID;
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
