package xyz.oli.api.pathing.strategy;

import lombok.NonNull;
import xyz.oli.api.wrapper.PathBlock;

public interface PathfinderStrategy {
    
    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param current The current block to check
     * @param previous Nullable, The previous block to check
     * @param previouser Nullable, The block before the previous to check
     */
    boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser);

}
