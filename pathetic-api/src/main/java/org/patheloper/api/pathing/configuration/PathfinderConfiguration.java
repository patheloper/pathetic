package org.patheloper.api.pathing.configuration;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import org.patheloper.api.annotation.Experimental;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.wrapper.PathPosition;

/**
 * Defines a set of configurable parameters that govern the behavior of the A* pathfinding
 * algorithm. By adjusting these parameters, you can fine-tune the pathfinding process to suit the
 * specific needs of your Minecraft environment.
 */
@With
@Value
@Getter
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PathfinderConfiguration {

  /**
   * The maximum number of iterations allowed for the pathfinding algorithm. This acts as a
   * safeguard to prevent infinite loops in complex scenarios.
   *
   * @default 5000
   */
  @Builder.Default int maxIterations = 5000;

  /**
   * The maximum permissible length of a calculated path (in blocks). Use this to constrain long
   * searches that could impact performance. A value of 0 indicates no limit.
   */
  int maxLength;

  /**
   * Determines whether pathfinding calculations should be executed asynchronously in a separate
   * thread. This can improve responsiveness in the main thread, but may introduce synchronization
   * complexities.
   */
  boolean async;

  /**
   * Controls whether the pathfinding algorithm can take diagonal steps. Enabling this allows for
   * more flexible and potentially shorter paths but might require a slightly more refined
   * heuristic.
   *
   * @default true
   */
  @Builder.Default boolean allowingDiagonal = true;

  /**
   * If set to true, the pathfinding process will terminate immediately if no path is found between
   * the start and target. This can be helpful for quick validation but prevents fallback
   * strategies.
   */
  boolean allowingFailFast;

  /**
   * If pathfinding fails, this parameter determines whether the algorithm should fall back to the
   * last successfully calculated path. This can help maintain progress, but might use an outdated
   * path.
   */
  boolean allowingFallback;

  /**
   * Controls whether chunks should be loaded or generated as needed during the pathfinding process.
   * This is essential for exploring uncharted areas, but may impact performance.
   */
  boolean loadingChunks;

  /**
   * Determines whether the pathfinding algorithm should see PathFilterStages as prioritization,
   * instead of filtering. This means that the pathfinding algorithm will prioritize paths that pass
   * the filters over paths that do not.
   *
   * <p>Setting this to true will no longer take the {@link org.patheloper.api.pathing.filter.PathFilterStage}s into the validation
   * process. Shared filters must still be passed.
   *
   * <p>{@link Pathfinder#findPath(PathPosition, PathPosition, List, List)}
   * @experimental This feature is experimental and may be subject to change.
   */
  @Experimental
  @Builder.Default boolean prioritizing = false;

  /**
   * The set of weights used to calculate heuristics within the A* algorithm. These influence the
   * pathfinding priority for distance, elevation changes, smoothness, and diagonal movement.
   *
   * @default HeuristicWeights.NATURAL_PATH_WEIGHTS
   */
  @Builder.Default HeuristicWeights heuristicWeights = HeuristicWeights.NATURAL_PATH_WEIGHTS;

  /**
   * @return A new {@link PathfinderConfiguration} with default parameters but async.
   */
  public static PathfinderConfiguration createAsyncConfiguration() {
    return builder().async(true).build();
  }

  /**
   * @return A new {@link PathfinderConfiguration} with default parameters.
   */
  public static PathfinderConfiguration createConfiguration() {
    return builder().build();
  }

  /**
   * Creates a deep copy of the given {@link PathfinderConfiguration}.
   *
   * <p>This method constructs a new instance of {@link PathfinderConfiguration} with the same
   * values as the input. It ensures a deep copy by copying the values of primitive and boolean
   * fields directly.
   *
   * @param pathfinderConfiguration The {@link PathfinderConfiguration} to copy.
   * @return A new {@link PathfinderConfiguration} instance with the same values as the input.
   */
  public static PathfinderConfiguration deepCopy(PathfinderConfiguration pathfinderConfiguration) {
    return builder()
        .maxIterations(pathfinderConfiguration.maxIterations)
        .maxLength(pathfinderConfiguration.maxLength)
        .async(pathfinderConfiguration.async)
        .allowingDiagonal(pathfinderConfiguration.allowingDiagonal)
        .allowingFailFast(pathfinderConfiguration.allowingFailFast)
        .allowingFallback(pathfinderConfiguration.allowingFallback)
        .loadingChunks(pathfinderConfiguration.loadingChunks)
        .heuristicWeights(pathfinderConfiguration.heuristicWeights)
        .build();
  }
}
