package xyz.ollieee.api.pathing.rules;

import lombok.Builder;
import lombok.Getter;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

@Getter
@Builder
public class PathingRuleSet {

    private PathfinderStrategy strategy;
    private int maxIterations;
    private int maxPathLength;
    private boolean async;
    private boolean allowDiagonal;

}


