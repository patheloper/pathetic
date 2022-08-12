package xyz.ollieee.api.pathing.rules;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

public class PathingRuleSet {

    private final PathfinderStrategy strategy;
    private final int maxIterations;
    private final int maxPathLength;
    private final boolean async;
    private final boolean allowDiagonal;

    PathingRuleSet(PathfinderStrategy strategy, int maxIterations, int maxPathLength, boolean async, boolean allowDiagonal) {
        this.strategy = strategy;
        this.maxIterations = maxIterations;
        this.maxPathLength = maxPathLength;
        this.async = async;
        this.allowDiagonal = allowDiagonal;
    }

    /**
     * Gets a builder for this rule set
     *
     * @return The {@link PathingRuleSetBuilder}
     */
    public static PathingRuleSetBuilder builder() {
        return new PathingRuleSetBuilder();
    }

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

    public static class PathingRuleSetBuilder {
        private PathfinderStrategy strategy;
        private int maxIterations;
        private int maxPathLength;
        private boolean async;
        private boolean allowDiagonal;

        PathingRuleSetBuilder() {
        }

        public PathingRuleSetBuilder strategy(PathfinderStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public PathingRuleSetBuilder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public PathingRuleSetBuilder maxPathLength(int maxPathLength) {
            this.maxPathLength = maxPathLength;
            return this;
        }

        public PathingRuleSetBuilder async(boolean async) {
            this.async = async;
            return this;
        }

        public PathingRuleSetBuilder allowDiagonal(boolean allowDiagonal) {
            this.allowDiagonal = allowDiagonal;
            return this;
        }

        public PathingRuleSet build() {
            return new PathingRuleSet(strategy, maxIterations, maxPathLength, async, allowDiagonal);
        }

        public String toString() {
            return "PathingRuleSet.PathingRuleSetBuilder(strategy=" + this.strategy + ", maxIterations=" + this.maxIterations + ", maxPathLength=" + this.maxPathLength + ", async=" + this.async + ", allowDiagonal=" + this.allowDiagonal + ")";
        }
    }
}


