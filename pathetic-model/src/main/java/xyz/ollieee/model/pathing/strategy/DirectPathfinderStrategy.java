package xyz.ollieee.model.pathing.strategy;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.StrategyEssentialsDao;
import xyz.ollieee.api.wrapper.PathBlock;

/**
 * A {@link PathfinderStrategy} to find the direct path to a given endpoint
 */
public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull StrategyEssentialsDao strategyEssentialsDao) {
        return strategyEssentialsDao.getSnapshotManager().getBlock(strategyEssentialsDao.getPathLocation()).isPassable();
    }

}
