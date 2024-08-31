package org.patheloper.api.pathing;

import java.util.List;
import java.util.concurrent.CompletionStage;
import lombok.NonNull;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.wrapper.PathPosition;

import javax.annotation.Nullable;

/**
 * A Pathfinder is a class that can find a path between two positions while following a given set of
 * rules.
 */
public interface Pathfinder {

  /**
   * Tries to find a Path between the two {@link PathPosition}'s provided with the given filters.
   *
   * @param filters A list of {@link PathFilter}'s to apply to the pathfinding process.
   * @return An {@link CompletionStage} that will contain a {@link PathfinderResult}.
   */
  @NonNull
  CompletionStage<PathfinderResult> findPath(
      @NonNull PathPosition start, @NonNull PathPosition target, @Nullable List<@NonNull PathFilter> filters);

  /**
   * Aborts the running pathfinding process.
   * <p>
   * In this context aborts means that the pathfinding process will be stopped and the result will
   * be {@link org.patheloper.api.pathing.result.PathState#ABORTED}.
   */
  void abort();
}
