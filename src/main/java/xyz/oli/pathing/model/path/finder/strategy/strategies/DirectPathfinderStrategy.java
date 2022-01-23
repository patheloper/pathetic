package xyz.oli.pathing.model.path.finder.strategy.strategies;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.pathing.model.wrapper.PathBlock;

public class DirectPathfinderStrategy extends PathfinderStrategy {

    @Override
    public boolean isValid(PathBlock current, PathBlock previous, PathBlock previouser) {
        return current.isEmpty();
    }

    @Override
    public boolean verifyEnd(PathBlock block) {
        return block.isEmpty();
    }

}
