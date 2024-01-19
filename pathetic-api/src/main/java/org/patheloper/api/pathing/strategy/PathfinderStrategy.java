package org.patheloper.api.pathing.strategy;

import lombok.NonNull;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link Pathfinder}.
 */
@FunctionalInterface
public interface PathfinderStrategy {

  /**
   * Returns whether the given {@link PathPosition} is valid.
   *
   * @param pathValidationContext The context providing essentials needed for path validation
   */
  boolean isValid(@NonNull PathValidationContext pathValidationContext);

  /**
   * Cleans up the resources used during the pathfinding process. This method is guaranteed to
   * always be called after pathfinding and should be overridden to ensure proper disposal of
   * resources. Users can rely on the fact that this method will be invoked post pathfinding to do
   * necessary clean-ups.
   */
  default void cleanup() {}
}
