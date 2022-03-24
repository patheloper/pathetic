package xyz.ollieee.model.finder;

import lombok.AllArgsConstructor;
import lombok.NonNull;
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

    @NonNull
    @Override
    public PathfinderSuccess getPathfinderSuccess() {
        return this.pathfinderSuccess;
    }

    @NonNull
    @Override
    public Path getPath() {
        return this.path;
    }
}
