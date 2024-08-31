package org.patheloper.api.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.wrapper.PathPosition;

import java.util.List;

/** An event called when a Pathfinder starts pathing. */
@Getter
@RequiredArgsConstructor
public class PathingStartFindEvent implements PathingEvent {

  @NonNull private final PathPosition start;
  @NonNull private final PathPosition target;
  @NonNull private final List<PathFilter> filters;
}
