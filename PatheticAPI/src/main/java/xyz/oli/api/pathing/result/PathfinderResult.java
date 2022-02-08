package xyz.oli.api.pathing.result;

public interface PathfinderResult {
    
    /**
     * @return The Path if found
     */
    Path getPath();
    
    /**
     * @return Boolean whether it was successful
     */
    boolean successful();
    
    /**
     * @return The Success as Enum
     */
    PathfinderSuccess getPathfinderSuccess();
}
