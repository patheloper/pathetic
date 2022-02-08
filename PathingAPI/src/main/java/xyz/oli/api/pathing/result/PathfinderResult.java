package xyz.oli.api.pathing.result;

public interface PathfinderResult {

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
}
