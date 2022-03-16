package xyz.ollieee.api.event;

import lombok.Getter;

import xyz.ollieee.api.pathing.result.PathfinderResult;

/**
 * An event called when a pathfinder finishes pathing. Therefore the result does not matter.
 * Means that the event is called even if the pathing fails.
 */
public class PathingFinishedEvent extends PathingEvent {

    @Getter
    PathfinderResult pathfinderResult;

    public PathingFinishedEvent(PathfinderResult result) {
        this.pathfinderResult = result;
    }
}
