package xyz.oli.api.pathing.result;

public interface PathfinderResult {
    
    /**
     * @return Path the Path
     */
    Path getPath();
    
    /**
     * @return Boolean whether it was successful
     */
    boolean successful();
    
    /**
     * Returns the success
     * @return PathfinderSuccess
     */
    PathfinderSuccess getPathfinderSuccess();
}
