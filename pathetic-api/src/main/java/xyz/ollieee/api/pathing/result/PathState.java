package xyz.ollieee.api.pathing.result;

public enum PathState {
    
    /**
     * The Path was successfully found for a given strategy
     */
    FOUND,
    /**
     * The Path wasn't found, either it reached its max search depth or it couldn't find more locations
     */
    FAILED,
    /**
     * Signifies that the pathfinder fell back during the pathfinding attempt
     */
    FALLBACK;

}
