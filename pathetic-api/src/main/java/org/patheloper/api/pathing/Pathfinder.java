package org.patheloper.api.pathing;

import java.util.concurrent.CompletionStage;
import lombok.NonNull;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.strategy.PathFilter;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A Pathfinder is a class that can find a path between two positions while following a given set of
 * rules.
 */
public interface Pathfinder {

  /**
   * Tries to find a Path between the two {@link PathPosition}'s provided with the given strategy.
   *
   * @param filter The {@link PathFilter} to use
   * @return An {@link CompletionStage} that will contain a {@link PathfinderResult}.
   */
  @NonNull
  CompletionStage<PathfinderResult> findPath(
      @NonNull PathPosition start,
      @NonNull PathPosition target,
      @NonNull PathFilter filter);
}
