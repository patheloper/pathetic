package xyz.oli.api.wrapper;

import lombok.Value;

@Value
public class PathBlock {
    
    PathBlockType pathBlockType;
    PathLocation pathLocation;
    
    public PathBlock(PathLocation pathLocation, PathBlockType pathBlockType) {
        this.pathLocation = pathLocation;
        this.pathBlockType = pathBlockType;
    }

    public boolean isEmpty() {
        return this.pathBlockType == PathBlockType.AIR;
    }

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
