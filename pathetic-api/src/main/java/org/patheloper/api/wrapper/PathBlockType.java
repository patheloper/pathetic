package org.patheloper.api.wrapper;

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
    public static PathBlockType fromMaterial(Material material) {

        // TODO: A Better AIR check and also TALL_GRASS doesn't work on older versions as it doesn't exist

        if (material == Material.AIR)
            return AIR;

        switch (material) {
            case WATER:
            case LAVA:
                return LIQUID;
            case GRASS:
            case TALL_GRASS:
                return OTHER;
            default:
                return SOLID;
        }
    }
}
