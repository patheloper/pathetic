package xyz.ollieee.model.pathing.result;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathState;

@AllArgsConstructor
public class PathfinderResultImpl implements PathfinderResult {
    
    private final PathState pathState;
    private final Path path;

    @Override
    public boolean successful() {
        return pathState != PathState.FAILED;
    }

    @Override
    @NonNull
    public PathState getPathState() {
        return this.pathState;
    }

    @NonNull
    @Override
    public Path getPath() {
        return this.path;
    }
}
