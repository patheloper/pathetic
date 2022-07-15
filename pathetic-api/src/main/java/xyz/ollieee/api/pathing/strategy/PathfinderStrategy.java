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
     * @param current The current {@link PathBlock} to check
     * @param previous The previous {@link PathBlock} to check
     * @param previouser The {@link PathBlock} before the previous to check
     */
    boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser);

}
