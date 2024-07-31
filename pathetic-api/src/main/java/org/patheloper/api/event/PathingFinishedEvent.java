package org.patheloper.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.patheloper.api.pathing.result.PathfinderResult;

/**
 * An event called when a pathfinder finishes pathing. Therefor, the result does not matter. Means
 * that the event is called even if the pathing fails.
 */
@Getter
@AllArgsConstructor
public class PathingFinishedEvent implements PathingEvent {

  @NonNull private final PathfinderResult pathfinderResult;
}
