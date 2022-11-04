package xyz.ollieee.model.pathing.result;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathfinderState;

@AllArgsConstructor
public class PathfinderResultImpl implements PathfinderResult {
    
    private final PathfinderState pathfinderState;
    private final Path path;

    @Override
    public boolean successful() {
        return pathfinderState != PathfinderState.FAILED;
    }

    @Override
    @NonNull
    public PathfinderState getPathfinderState() {
        return this.pathfinderState;
    }

    @NonNull
    @Override
    public Path getPath() {
        return this.path;
    }
}
