package xyz.ollieee.api.pathing.rules;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;

public class PathingRuleSet {

    private PathfinderStrategy strategy;
    private int maxIterations;
    private int maxPathLength;
    private boolean async;
    private boolean allowDiagonal;

    PathingRuleSet(PathfinderStrategy strategy, int maxIterations, int maxPathLength, boolean async, boolean allowDiagonal) {
        this.strategy = strategy;
        this.maxIterations = maxIterations;
        this.maxPathLength = maxPathLength;
        this.async = async;
        this.allowDiagonal = allowDiagonal;
    }

    public static PathingRuleSetBuilder builder() {
        return new PathingRuleSetBuilder();
    }

    public PathfinderStrategy getStrategy() {
        return this.strategy;
    }

    public int getMaxIterations() {
        return this.maxIterations;
    }

    public int getMaxPathLength() {
        return this.maxPathLength;
    }

    public boolean isAsync() {
        return this.async;
    }

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


