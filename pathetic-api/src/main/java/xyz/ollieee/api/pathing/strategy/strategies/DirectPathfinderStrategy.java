package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategyEssentials;

/**
 * A {@link PathfinderStrategy} to find the direct path to a given endpoint
 */
public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathfinderStrategyEssentials pathfinderStrategyEssentials) {
        return pathfinderStrategyEssentials.getSnapshotManager().getBlock(pathfinderStrategyEssentials.getPathLocation()).isPassable();
    }

}
