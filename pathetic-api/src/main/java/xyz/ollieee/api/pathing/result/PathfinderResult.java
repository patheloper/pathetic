package xyz.ollieee.api.pathing.result;

import lombok.NonNull;

public interface PathfinderResult {
    
    /**
     * Returns the found {@link Path} regardless if successful or not.
     * The path is empty if the pathfinding failed.
     *
     * @return The found {@link Path}
     */
    @NonNull
    Path getPath();
    
    boolean successful();
    
    @NonNull
    PathfinderState getPathfinderState();
}
