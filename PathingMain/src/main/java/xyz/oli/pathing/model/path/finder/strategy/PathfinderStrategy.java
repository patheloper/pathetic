package xyz.oli.pathing.model.path.finder.strategy;

import lombok.NonNull;
import xyz.oli.wrapper.PathBlock;

public interface PathfinderStrategy {

    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param current The current block to check
     * @param previous The previous location
     * @param previouser The previous previous location
     */
    boolean isValid(@NonNull PathBlock current, @NonNull PathBlock previous, @NonNull PathBlock previouser);

    /**
     * Implement the logic to see if a start/target is valid
     *
     * @param location The location to check
     */
    boolean verifyEnd(@NonNull PathBlock location);
}
