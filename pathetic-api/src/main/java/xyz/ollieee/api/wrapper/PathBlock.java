package xyz.ollieee.api.wrapper;

import lombok.Getter;
import lombok.Value;

/**
 * A Class to represent a block in the world, except exempt of Bukkit
 */
@Value
@Getter
public class PathBlock {

    PathBlockType pathBlockType;
    PathLocation pathLocation;
    
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

    public int getBlockX() {
        return this.pathLocation.getBlockX();
    }

    public int getBlockY() {
        return this.pathLocation.getBlockY();
    }

    public int getBlockZ() {
        return this.pathLocation.getBlockZ();
    }
}
