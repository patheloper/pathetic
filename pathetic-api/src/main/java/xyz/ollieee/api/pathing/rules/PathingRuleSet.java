package xyz.ollieee.api.pathing.rules;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

@Builder
@Value
@RequiredArgsConstructor
public class PathingRuleSet {

    PathfinderStrategy strategy;
    int maxIterations;
    int maxPathLength;
    boolean async;
    boolean allowDiagonal;

    /**
     * Returns the {@link PathfinderStrategy} for this rule set
     *
     * @return The {@link PathfinderStrategy}
     */
    public PathfinderStrategy getStrategy() {
        return this.strategy;
    }

    /**
     * Returns the maximum number of iterations for this rule set
     *
     * @return The maximum number of iterations
     */
    public int getMaxIterations() {
        return this.maxIterations;
    }

    /**
     * Returns the maximum path length for this rule set
     *
     * @return The maximum path length
     */
    public int getMaxPathLength() {
        return this.maxPathLength;
    }

    /**
     * Returns whether this rule set is asynchronous
     *
     * @return true if asynchronous
     */
    public boolean isAsync() {
        return this.async;
    }

    /**
     * Returns whether this rule set allows diagonal movement
     *
     * @return true if diagonal movement is allowed
     */
    public boolean isAllowDiagonal() {
        return this.allowDiagonal;
    }
}


