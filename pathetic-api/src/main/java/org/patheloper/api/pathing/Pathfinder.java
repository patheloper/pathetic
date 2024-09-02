package org.patheloper.api.pathing;

import java.util.List;
import java.util.concurrent.CompletionStage;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathFilterContainer;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.wrapper.PathPosition;

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
      @NonNull PathPosition start,
      @NonNull PathPosition target,
      @Nullable List<@NonNull PathFilter> filters);

  /**
   * Tries to find a Path between the two {@link PathPosition}'s provided with the given
   * filter-containers.
   *
   * <p>Other than the {@link #findPath(PathPosition, PathPosition, List)} method, this method
   * allows for more complex filtering by using {@link PathFilterContainer}'s.
   *
   * <p>The filters in the containers will be applied in the order they are provided. If a filter in
   * a container returns false, the next container will be checked. Only one container needs to
   * return true for the path to be considered valid.
   *
   * @api.Note The containers will be checked in the order they are provided.
   * @param start The start position of the path.
   * @param target The target position of the path.
   * @param filterContainers A list of {@link PathFilterContainer}'s to apply to the pathfinding
   * @return An {@link CompletionStage} that will contain a {@link PathfinderResult}.
   */
  @NonNull
  CompletionStage<PathfinderResult> findPreferredPath(
      @NonNull PathPosition start,
      @NonNull PathPosition target,
      @Nullable List<@NonNull PathFilterContainer> filterContainers);

  /**
   * Aborts the running pathfinding process.
   *
   * <p>In this context aborts means that the pathfinding process will be stopped and the result
   * will be {@link org.patheloper.api.pathing.result.PathState#ABORTED}.
   */
  void abort();
}
