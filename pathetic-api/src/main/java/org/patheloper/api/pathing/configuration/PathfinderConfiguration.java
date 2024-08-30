package org.patheloper.api.pathing.configuration;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;

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
   * If pathfinding fails, determines whether to run a reverse pathfinding check (from target to
   * start) to verify the result. This is a computationally expensive fallback but can help identify
   * some failure cases.
   */
  boolean counterCheck;

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
        .counterCheck(pathfinderConfiguration.counterCheck)
        .heuristicWeights(pathfinderConfiguration.heuristicWeights)
        .build();
  }
}
