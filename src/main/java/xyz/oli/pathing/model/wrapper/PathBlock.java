package xyz.oli.pathing.model.wrapper;

public class PathBlock {
    
    private final PathBlockType pathBlockType;
    private final PathLocation pathLocation;
    
    private PathBlock(PathLocation pathLocation, PathBlockType pathBlockType) {
        this.pathLocation = pathLocation;
        this.pathBlockType = pathBlockType;
    }
    
}
