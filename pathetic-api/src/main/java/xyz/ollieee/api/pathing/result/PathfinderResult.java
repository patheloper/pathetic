package xyz.ollieee.api.pathing.result;

import lombok.NonNull;

public interface PathfinderResult {

    /**
     * Returns whether the pathfinding was successful.
     *
     * @return true if the pathfinding was successful
     */
    boolean successful();

    /**
     * Returns the state of the pathfinding.
     *
     * @return The {@link PathState}
     */
    @NonNull
    PathState getPathState();

    /**
     * Returns the found {@link Path} regardless if successful or not.
     * The path is empty if the pathfinding failed.
     *
     * @return The found {@link Path}
     */
    @NonNull
    Path getPath();
}
