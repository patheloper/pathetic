package xyz.ollieee.api.event;

import lombok.NonNull;
import xyz.ollieee.api.pathing.result.PathfinderResult;

/**
 * An event called when a pathfinder finishes pathing. Therefore, the result does not matter.
 * Means that the event is called even if the pathing fails.
 */
public class PathingFinishedEvent extends PathingEvent {

    PathfinderResult pathfinderResult;

    public PathingFinishedEvent(@NonNull PathfinderResult result) {
        this.pathfinderResult = result;
    }

    /**
     * Gets the pathfinder result
     * @return {@link PathfinderResult} the pathfinder result
     */
    @NonNull
    public PathfinderResult getPathfinderResult() {
        return this.pathfinderResult;
    }
}
