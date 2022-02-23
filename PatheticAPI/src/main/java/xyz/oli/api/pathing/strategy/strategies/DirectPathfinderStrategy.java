package xyz.oli.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.wrapper.PathBlock;

public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser) {
        return current.isEmpty();
    }

}
