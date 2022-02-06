package xyz.oli.pathing.model.path.finder.pathfinder;

import lombok.AllArgsConstructor;
import xyz.oli.pathing.Path;
import xyz.oli.pathing.PathResult;

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
    public Path getPath() {
        return this.path;
    }
}
