package xyz.ollieee.api.pathing.result;

import lombok.NonNull;

public interface PathfinderResult {
    
    /**
     * @return The {@link Path} if found
     */
    @NonNull
    Path getPath();
    
    /**
     * @return whether it was successful
     */
    boolean successful();
    
    /**
     * @return The success as a {@link PathfinderSuccess}
     */
    @NonNull
    PathfinderSuccess getPathfinderSuccess();
}
