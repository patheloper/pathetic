package xyz.ollieee.api.wrapper;

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
}
