package org.patheloper.api.pathing.result;

public enum PathState {
    
    /**
     * The Path was successfully found for a given strategy
     */
    FOUND,
    /**
     * The Path wasn't found, either it reached its max search depth or it couldn't find more positions
     */
    FAILED,
    /**
     * Signifies that the pathfinder fell back during the pathfinding attempt
     */
    FALLBACK,
    /**
     * Signifies that the pathfinder reached its length limit
     */
    LENGTH_LIMITED,
    /**
     * Signifies that the pathfinder reached its iteration limit
     */
    MAX_ITERATIONS_REACHED;

    /**
     * Whether the pathfinder has failed to reach its target. This includes
     * {@link #FAILED}, {@link #LENGTH_LIMITED}, {@link #MAX_ITERATIONS_REACHED} and {@link #FALLBACK}
     *
     * @return Whether the pathfinder has failed to reach its target
     */
    public boolean hasFailed() {
        return this == FAILED || this == LENGTH_LIMITED || this == MAX_ITERATIONS_REACHED || this == FALLBACK;
    }

    /**
     * Whether the pathfinder has successfully reached its target. This includes {@link #FOUND}
     *
     * @return Whether the pathfinder has successfully reached its target
     */
    public boolean wasSuccessful() {
        return this == FOUND;
    }

}
