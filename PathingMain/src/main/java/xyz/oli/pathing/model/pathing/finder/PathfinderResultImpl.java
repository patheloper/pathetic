package xyz.oli.pathing.model.pathing.finder;

import lombok.AllArgsConstructor;
import xyz.oli.pathing.Path;

@AllArgsConstructor
public class PathfinderResultImpl implements xyz.oli.pathing.PathfinderResult {
    
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
