package xyz.ollieee.model.finder;

import lombok.AllArgsConstructor;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathfinderSuccess;

@AllArgsConstructor
public class PathfinderResultImpl implements PathfinderResult {
    
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
