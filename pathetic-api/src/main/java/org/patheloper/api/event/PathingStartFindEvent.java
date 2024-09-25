package org.patheloper.api.event;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathFilterStage;
import org.patheloper.api.wrapper.PathPosition;

/**
 * An event triggered when a pathfinder begins the pathfinding process.
 */
@Getter
@RequiredArgsConstructor
public class PathingStartFindEvent implements PathingEvent {

  /** The starting {@link PathPosition} of the pathfinding process. */
  @NonNull private final PathPosition start;

  /** The target {@link PathPosition} of the pathfinding process. */
  @NonNull private final PathPosition target;

  /** A list of {@link PathFilter} objects to be applied during the pathfinding process. */
  @NonNull private final List<PathFilter> filters;

  /**
   * A list of {@link PathFilterStage} objects representing different stages of the filtering
   * process.
   */
  @NonNull private final List<PathFilterStage> filterStages;
}
