package xyz.oli.pathing.model.path.finder;

import lombok.AllArgsConstructor;
import xyz.oli.pathing.PathResult;
import xyz.oli.pathing.model.path.Path;

@AllArgsConstructor
public class PathfinderResult implements PathResult {
    
    PathfinderSuccess pathfinderSuccess;
    Path path;

    @Override
    public boolean successful() {
        return pathfinderSuccess == PathfinderSuccess.FOUND;
    }

    @Override
    public PathfinderSuccess getPathfinderSuccess() {
        return this.pathfinderSuccess;
    }

    @Override
    public xyz.oli.pathing.Path getPath() {
        return this.path;
    }
}
