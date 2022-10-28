package xyz.ollieee.api.pathing.rules;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

@Builder
@Value
@Getter
@RequiredArgsConstructor
/**
 * A set of rules that are used while pathfinding.
 *
 * strategy - The strategy to use while pathfinding.
 * maxIterations - The maximum amount of iterations to do while pathfinding.
 * maxPathLength - The maximum length of the path to find.
 * async - Whether to run the pathfinding async or not.
 * allowDiagonal - Whether to allow diagonal movement or not.
 * alternateTarget - Whether to allow the pathfinder to end at a different block than the target if the target is unreachable.
 * failFast - Whether to fail fast or not, if the target is unreachable by the beginning.
 * fallback - If pathfinding fails, it will fallback to the already found path.
 */
public class PathingRuleSet {

    PathfinderStrategy strategy;
    int maxIterations;
    int maxPathLength;
    boolean async;
    boolean allowDiagonal;
    boolean allowAlternateTarget;
    boolean allowFailFast;
    boolean allowFallback;
}


