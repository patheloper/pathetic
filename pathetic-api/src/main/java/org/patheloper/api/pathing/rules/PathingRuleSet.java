package org.patheloper.api.pathing.rules;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;

/**
 * Configuration options for pathfinding.
 *
 * <p>This class defines a set of rules that guide the behavior of the pathfinding process.
 *
 * <p>- `maxIterations`: The maximum number of iterations allowed during pathfinding. Set this to
 * prevent infinite loops.
 *
 * <p>- `maxLength`: The maximum length of the path. Avoid setting this too high as it can cause
 * performance issues.
 *
 * <p>- `async`: Whether to run pathfinding asynchronously or not.
 *
 * <p>- `allowingDiagonal`: Whether to allow diagonal movement when pathfinding.
 *
 * <p>- `allowingFailFast`: Whether to fail fast if the target is unreachable from the start.
 *
 * <p>- `allowingFallback`: If pathfinding fails, whether to fall back to the previously found path.
 *
 * <p>- `loadingChunks`: Whether to load or generate chunks during pathfinding. This will be ignored on paper
 * and always load chunks.
 *
 * <p>- `counterCheck`: Whether to run a counter check on the path if it's not found to validate the
 * result. Note: `counterCheck` is a fallback mechanism that reevaluates the entire path from end to
 * beginning.
 */
@With
@Value
@Getter
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PathingRuleSet {

  @Builder.Default int maxIterations = 5000; // to avoid freewheeling
  int maxLength;
  boolean async;
  @Builder.Default boolean allowingDiagonal = true;
  boolean allowingFailFast;
  boolean allowingFallback;
  boolean loadingChunks;
  boolean counterCheck;

  /**
   * @return A new {@link PathingRuleSet} with default values but async.
   */
  public static PathingRuleSet createAsyncRuleSet() {
    return builder().async(true).build();
  }

  /**
   * @return A new {@link PathingRuleSet} with default values.
   */
  public static PathingRuleSet createRuleSet() {
    return builder().build();
  }

  /**
   * Creates a deep copy of the given {@link PathingRuleSet}.
   *
   * <p>This method constructs a new instance of {@link PathingRuleSet} with the same values
   * as the input. It ensures a deep copy by copying the values of primitive and boolean fields directly.
   *
   * @param pathingRuleSet The {@link PathingRuleSet} to copy.
   * @return A new {@link PathingRuleSet} instance with the same values as the input.
   */
  public static PathingRuleSet deepCopy(PathingRuleSet pathingRuleSet) {
    return builder()
      .maxIterations(pathingRuleSet.maxIterations)
      .maxLength(pathingRuleSet.maxLength)
      .async(pathingRuleSet.async)
      .allowingDiagonal(pathingRuleSet.allowingDiagonal)
      .allowingFailFast(pathingRuleSet.allowingFailFast)
      .allowingFallback(pathingRuleSet.allowingFallback)
      .loadingChunks(pathingRuleSet.loadingChunks)
      .counterCheck(pathingRuleSet.counterCheck)
      .build();
  }
}
