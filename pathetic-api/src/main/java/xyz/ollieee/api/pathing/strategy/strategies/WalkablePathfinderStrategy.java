package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.StrategyData;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathBlockType;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * A {@link PathfinderStrategy} to find a walkable path to a given endpoint
 *
 * @deprecated Use WIP
 */
@Deprecated
public class WalkablePathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull StrategyData essentials) {

        PathBlock pathBlock = essentials.getBlock(essentials.getPathLocation());

        PathLocation below = pathBlock.getPathLocation().clone().subtract(0, 1, 0);
        PathLocation above = pathBlock.getPathLocation().clone().add(0, 1, 0);
        PathLocation aboveAbove = above.clone().add(0, 1, 0);

        return pathBlock.isPassable()
                && essentials.getBlock(below).getPathBlockType() == PathBlockType.SOLID
                && essentials.getBlock(above).isPassable()
                && essentials.getBlock(aboveAbove).isPassable();
    }
}
