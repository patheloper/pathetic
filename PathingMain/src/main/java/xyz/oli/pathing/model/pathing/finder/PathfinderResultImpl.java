package xyz.oli.pathing.model.pathing.finder;

import lombok.AllArgsConstructor;
import xyz.oli.api.pathing.Path;
import xyz.oli.api.pathing.PathfinderResult;

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
