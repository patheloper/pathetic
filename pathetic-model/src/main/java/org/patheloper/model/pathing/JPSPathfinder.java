package org.patheloper.model.pathing;

import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;

/**
 * A pathfinder that uses the Jump Point Search algorithm.
 */
public class JPSPathfinder extends AbstractPathfinder {

    protected JPSPathfinder(PathingRuleSet pathingRuleSet) {
        super(pathingRuleSet);
    }

    @Override
    protected PathfinderResult findPath(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
