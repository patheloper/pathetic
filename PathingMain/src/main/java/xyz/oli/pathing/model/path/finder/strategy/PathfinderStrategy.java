package xyz.oli.pathing.model.path.finder.strategy;

import xyz.oli.pathing.model.wrapper.PathBlock;

import org.jetbrains.annotations.NotNull;

public abstract class PathfinderStrategy {

    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param current The current block to check
     * @param previous The previous location
     * @param previouser The previous previous location
     */
    public abstract boolean isValid(@NotNull PathBlock current, @NotNull PathBlock previous, @NotNull PathBlock previouser);

    /**
     * Implement the logic to see if a start/target is valid
     *
     * @param location The location to check
     */
    public abstract boolean verifyEnd(@NotNull PathBlock location);
}
