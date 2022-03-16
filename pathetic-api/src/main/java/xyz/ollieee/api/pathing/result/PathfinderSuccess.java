package xyz.ollieee.api.pathing.result;

public enum PathfinderSuccess {
    
    /**
     * The Path was successfully found for a given strategy
     */
    FOUND,
    /**
     * The Path wasn't found, either it reached its max search depth or it couldn't find more locations
     */
    FAILED,
    
    /**
     * The Path wasn't found, It couldn't verify the start and finish locations
     */
    INVALID,
    
    /**
     * The Pathfinder got cancelled through {@link xyz.ollieee.api.event.PathingStartFindEvent}
     */
    CANCELLED
    
}
