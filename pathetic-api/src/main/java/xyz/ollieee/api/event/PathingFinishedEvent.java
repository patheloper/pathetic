package xyz.ollieee.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.PathfinderResult;

/**
 * An event called when a pathfinder finishes pathing. Therefore, the result does not matter.
 * Means that the event is called even if the pathing fails.
 */
@AllArgsConstructor
public class PathingFinishedEvent extends PathingEvent {

    @NonNull
    @Getter
    private final PathfinderResult pathfinderResult;
}
