package org.patheloper.api.pathing.rules;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;

/**
 * A set of rules that are used while pathfinding.
 *
 * strategy - The class of the strategy to use while pathfinding.
 *
 * maxIterations - The maximum amount of iterations to do while pathfinding.
 *
 * maxLength - The maximum length of the path.
 * This should not be set too high since it can cause a lagspike within the pathfinder which causes it to take longer.
 *
 * maxRecursionDepth - The maximum recursion depth to use while pathfinding.
 *
 * async - Whether to run the pathfinding async or not.
 *
 * allowRecursiveFinding - Whether to allow recursive pathfinding or not.
 * NOTE: This can be a very expensive operation and should only be used if you know what you are doing.
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

    private static final Class<? extends PathfinderStrategy> DEFAULT_STRATEGY = DirectPathfinderStrategy.class;

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

    @Builder.Default
    Class<? extends PathfinderStrategy> strategy = DEFAULT_STRATEGY;
    @Builder.Default
    int maxIterations = 5000; // to avoid freewheeling
    int maxLength;
    @Builder.Default
    int maxRecursionDepth = 100;
    boolean async;
    boolean allowingRecursiveFinding;
    boolean allowingDiagonal;
    boolean allowingAlternateTarget;
    boolean allowingFailFast;
    boolean allowingFallback;
    boolean loadingChunks;
}


