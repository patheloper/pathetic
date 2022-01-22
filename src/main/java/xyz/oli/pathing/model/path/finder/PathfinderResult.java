package xyz.oli.pathing.model.path.finder;

import xyz.oli.pathing.model.path.Path;

public class PathfinderResult {
    
    private final PathfinderSuccess pathfinderSuccess;
    private final Path path;
    
    public PathfinderResult(PathfinderSuccess success, Path path) {
        this.pathfinderSuccess = success;
        this.path = path;
    }
    
    public Path getPath() {
        return path;
    }
    
    public boolean successful() {
        return pathfinderSuccess == PathfinderSuccess.FOUND;
    }

    public PathfinderSuccess getPathfinderSuccess() {
        return this.pathfinderSuccess;
    }
}

enum PathfinderSuccess {
    FOUND,
    FAILED
}
