package xyz.oli.pathing.model.pathing.finder.strategies;

import lombok.NonNull;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.wrapper.PathBlock;

public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, @NonNull PathBlock previous, @NonNull PathBlock previouser) {
        return current.isEmpty();
    }

    @Override
    public boolean endIsValid(@NonNull PathBlock block) {
        return block.isEmpty();
    }

}
