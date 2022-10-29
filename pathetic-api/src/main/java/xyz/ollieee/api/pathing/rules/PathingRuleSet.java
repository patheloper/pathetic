package xyz.ollieee.api.pathing.rules;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

/**
 * A set of rules that are used while pathfinding.
 * <p>
 * strategy - The strategy to use while pathfinding.
 * <p>
 * maxIterations - The maximum amount of iterations to do while pathfinding.
 * <p>
 * maxPathLength - The maximum length of the path to find.
 * <p>
 * async - Whether to run the pathfinding async or not.
 * <p>
 * allowDiagonal - Whether to allow diagonal movement or not.
 * <p>
 * alternateTarget - Whether to allow the pathfinder to end at a different block than the target if the target is unreachable.
 * NOTE: In most cases fallback is the better option since this can be in the worst case a very expensive radical operation
 * in addition to the regular pathfinding with only the goal to find an alternate target
 * no matter where or in what relation to anything.
 * <p>
 * failFast - Whether to fail fast or not, if the target is unreachable by the beginning.
 * <p>
 * fallback - If pathfinding fails, it will fallback to the already found path.
 * <p>
 * loadChunks - Whether to load / generate chunks
 */
@Builder
@Value
@Getter
@RequiredArgsConstructor
public class PathingRuleSet {

    public static final PathingRuleSet DEFAULT_RULE_SET = PathingRuleSet.builder().build();

    PathfinderStrategy strategy;
    @Builder.Default
    int maxIterations = Integer.MAX_VALUE;
    @Builder.Default
    int maxPathLength = Integer.MAX_VALUE;
    boolean async;
    boolean allowDiagonal;
    boolean allowAlternateTarget;
    boolean allowFailFast;
    boolean allowFallback;
    boolean loadChunks;
}


