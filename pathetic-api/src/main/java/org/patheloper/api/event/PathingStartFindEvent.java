package org.patheloper.api.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.patheloper.api.pathing.strategy.PathFilter;
import org.patheloper.api.wrapper.PathPosition;

import java.util.List;

/** An event called when a Pathfinder starts pathing. */
@RequiredArgsConstructor
public class PathingStartFindEvent extends PathingEvent implements Cancellable {

  @Getter private final PathPosition start;
  @Getter private final PathPosition target;
  @NonNull @Getter private final List<PathFilter> filters;
  private boolean cancelled = false;

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
}
