package xyz.oli.pathing;

public interface PathResult {

    /**
     * Returns the success
     * @return PathfinderSuccess
     */
    PathfinderSuccess getPathfinderSuccess();

    /**
     * @return Path the Path
     */
    Path getPath();

    /**
     * @return Boolean whether it was successful
     */
    boolean successful();

    enum PathfinderSuccess {
        /**
         * The Path was successfully found for a given strategy
         */
        FOUND,
        /**
         * The Path wasn't found, either the start/finish were invalid, it reached its max search depth, or it couldn't find more locations
         */
        FAILED
    }
}
