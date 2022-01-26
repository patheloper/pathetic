package xyz.oli.pathing.model.wrapper;

public class PathBlock {
    
    private final PathBlockType pathBlockType;
    private final PathLocation pathLocation;
    
    public PathBlock(PathLocation pathLocation, PathBlockType pathBlockType) {
        this.pathLocation = pathLocation;
        this.pathBlockType = pathBlockType;
    }

    public PathLocation getPathLocation() {
        return this.pathLocation;
    }

    public PathBlockType getPathBlockType() {
        return this.pathBlockType;
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
