package xyz.ollieee.api.pathing.strategy;

import lombok.NonNull;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link xyz.ollieee.api.pathing.Pathfinder}
 */
@FunctionalInterface
public interface PathfinderStrategy {
    
    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param pathfinderStrategyEssentials The {@link PathfinderStrategyEssentials} to access the needed essentials for a strategy
     */
    boolean isValid(@NonNull PathfinderStrategyEssentials pathfinderStrategyEssentials);

}
