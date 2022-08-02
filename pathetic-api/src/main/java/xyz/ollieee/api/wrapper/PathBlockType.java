package xyz.ollieee.api.wrapper;

import org.bukkit.Material;

/**
 * Enum to represent the block type of wrapped blocks
 */
public enum PathBlockType {

    /**
     * Represents an air block
     */
    AIR,
    /**
     * A block that is either Lava or Water
     */
    LIQUID,
    /**
     * Anything that is deemed not passable
     */
    SOLID,
    /**
     * Represents all blocks that could be walked through, but are not explicitly air
     */
    OTHER;

    /**
     * @return {@link PathBlockType} the block type of the given block
     */
    public static PathBlockType getBlockType(Material material) {

        if (material.isAir())
            return PathBlockType.AIR;

        switch (material) {
            case WATER:
            case LAVA:
                return PathBlockType.LIQUID;
            case GRASS:
            case TALL_GRASS:
                return PathBlockType.OTHER;
            default:
                return PathBlockType.SOLID;
        }
    }
}
