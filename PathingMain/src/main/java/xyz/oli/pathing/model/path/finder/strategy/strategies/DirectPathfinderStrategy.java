package xyz.oli.pathing.model.path.finder.strategy.strategies;


import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.pathing.model.wrapper.PathBlock;

import org.jetbrains.annotations.NotNull;

public class DirectPathfinderStrategy extends PathfinderStrategy {

    @Override
    public boolean isValid(@NotNull PathBlock current, @NotNull PathBlock previous, @NotNull PathBlock previouser) {
        return current.isEmpty();
    }

    @Override
    public boolean verifyEnd(@NotNull PathBlock block) {
        return block.isEmpty();
    }

}
