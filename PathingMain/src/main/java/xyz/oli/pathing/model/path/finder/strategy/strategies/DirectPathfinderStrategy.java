package xyz.oli.pathing.model.path.finder.strategy.strategies;

import lombok.NonNull;
import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.wrapper.PathBlock;

public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, @NonNull PathBlock previous, @NonNull PathBlock previouser) {
        return current.isEmpty();
    }

    @Override
    public boolean verifyEnd(@NonNull PathBlock block) {
        return block.isEmpty();
    }

}
