package xyz.ollieee.api.wrapper;

import java.util.Objects;

/**
 * A Class to represent a block in the world, except exempt of Bukkit
 */
public final class PathBlock {

    private final PathBlockType pathBlockType;
    private final PathLocation pathLocation;

    public PathBlock(PathLocation pathLocation, PathBlockType pathBlockType) {
        this.pathLocation = pathLocation;
        this.pathBlockType = pathBlockType;
    }

    /**
     * @return Whether the block is air
     */
    public boolean isEmpty() {
        return this.pathBlockType == PathBlockType.AIR;
    }

    /**
     * @return Whether the block is possible to walk through
     */
    public boolean isPassable() {
        return this.pathBlockType == PathBlockType.AIR || this.pathBlockType == PathBlockType.OTHER;
    }

    /**
     * Gets the X coordinate of the block
     *
     * @return The X coordinate of the block
     */
    public int getBlockX() {
        return this.pathLocation.getBlockX();
    }

    /**
     * Gets the Y coordinate of the block
     *
     * @return The Y coordinate of the block
     */
    public int getBlockY() {
        return this.pathLocation.getBlockY();
    }

    /**
     * Gets the Z coordinate of the block
     *
     * @return The Z coordinate of the block
     */
    public int getBlockZ() {
        return this.pathLocation.getBlockZ();
    }

    /**
     * Gets the {@link PathBlockType} of the block
     *
     * @return The {@link PathBlockType} of the block
     */
    public PathBlockType getPathBlockType() {
        return this.pathBlockType;
    }

    /**
     * Gets the {@link PathLocation} of the block
     *
     * @return The {@link PathLocation} of the block
     */
    public PathLocation getPathLocation() {
        return this.pathLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathBlock pathBlock = (PathBlock) o;
        return pathBlockType == pathBlock.pathBlockType && Objects.equals(pathLocation, pathBlock.pathLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathBlockType, pathLocation);
    }

    public String toString() {
        return "PathBlock(pathBlockType=" + this.getPathBlockType() + ", pathLocation=" + this.getPathLocation() + ")";
    }
}
