package xyz.oli.pathing.model.path.finder.strategy.strategies;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.pathing.model.wrapper.PathBlock;

public class DirectPathfinderStrategy extends PathfinderStrategy {

    @Override
    public boolean isValid(PathBlock current, PathBlock previous, PathBlock previouser) {
        return current.isEmpty();
    }

    @Override
    public boolean verifyEnd(PathBlock location) {
        System.out.println("valid end: " + location.isEmpty());
        System.out.println(location.getPathBlockType());
        System.out.println("valid end: " + location.getPathLocation());
        return location.isEmpty();
    }

}
