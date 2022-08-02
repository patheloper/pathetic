package xyz.ollieee.api.pathing.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

public class PathingRuleSetBuilder {

    private PathLocation start;
    private PathLocation target;
    private PathfinderStrategy strategy;

    public PathingRuleSetBuilder setStart(PathLocation start) {
        this.start = start;
        return this;
    }

    public PathingRuleSetBuilder setTarget(PathLocation target) {
        this.target = target;
        return this;
    }

    public PathingRuleSetBuilder setStrategy(PathfinderStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public PathingRuleSet build() {
        if (start == null || target == null) {
            throw new IllegalStateException("Start and target must be set");
        }
        return new PathingRuleSet(start, target, strategy);
    }

    @Getter
    @AllArgsConstructor
    public static class PathingRuleSet {
        PathLocation start;
        PathLocation target;
        PathfinderStrategy strategy;
    }
}


