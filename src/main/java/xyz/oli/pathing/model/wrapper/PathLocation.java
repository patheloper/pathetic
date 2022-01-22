package xyz.oli.pathing.model.wrapper;

public class PathLocation {

    private final PathWorld pathWorld;
    private int x, y, z;
    
    public PathLocation(PathWorld pathWorld, int x, int y, int z) {
        
        this.pathWorld = pathWorld;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PathWorld getPathWorld() {
        return this.pathWorld;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

}
