package xyz.ollieee.api.pathing.rules;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.strategies.DirectPathfinderStrategy;

/**
 * A set of rules that are used while pathfinding.
 *
 * strategy - The strategy to use while pathfinding.
 *
 * maxIterations - The maximum amount of iterations to do while pathfinding.
 *
 * async - Whether to run the pathfinding async or not.
 *
 * allowDiagonal - Whether to allow diagonal movement or not.
 *
 * alternateTarget - Whether to allow the pathfinder to end at a different block than the target if the target is unreachable.
 * NOTE: In most cases fallback is the better option since this can be in the worst case a very expensive radical operation
 * in addition to the regular pathfinding with only the goal to find an alternate target
 * no matter where or in what relation to anything.
 *
 * failFast - Whether to fail fast or not, if the target is unreachable by the beginning.
 *
 * fallback - If pathfinding fails, it will fallback to the already found path.
 *
 * loadChunks - Whether to load / generate chunks
 */
@With
@Value
@Getter
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PathingRuleSet {

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

    private static final PathfinderStrategy DEFAULT_STRATEGY = new DirectPathfinderStrategy();

    @Builder.Default
    PathfinderStrategy strategy = DEFAULT_STRATEGY;
    @Builder.Default
    int maxIterations = 5000; // to avoid freewheeling
    int maxLength;
    boolean async;
    boolean allowingDiagonal;
    boolean allowingAlternateTarget;
    boolean allowingFailFast;
    boolean allowingFallback;
    boolean loadingChunks;
}


