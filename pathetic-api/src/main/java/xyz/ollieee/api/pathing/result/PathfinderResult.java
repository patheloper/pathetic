package xyz.ollieee.api.pathing.result;

public interface PathfinderResult {
    
    /**
     * @return The {@link Path} if found
     */
    Path getPath();
    
    /**
     * @return whether it was successful
     */
    boolean successful();
    
    /**
     * @return The success as a {@link PathfinderSuccess}
     */
    PathfinderSuccess getPathfinderSuccess();
}
