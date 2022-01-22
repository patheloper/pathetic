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
}
