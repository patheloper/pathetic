package xyz.ollieee.api.pathing.strategy;

import lombok.NonNull;
import xyz.ollieee.api.wrapper.PathBlock;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link xyz.ollieee.api.pathing.Pathfinder}
 */
@FunctionalInterface
public interface PathfinderStrategy {
    
    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param strategyEssentialsDao The {@link xyz.ollieee.api.pathing.strategy.StrategyEssentialsDao} to access the needed essentials for a strategy
     */
    boolean isValid(@NonNull StrategyEssentialsDao strategyEssentialsDao);

}
