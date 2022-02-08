package xyz.oli.pathing.model.pathing.finder.factory;

import xyz.oli.api.options.PathfinderOptions;
import xyz.oli.api.pathing.Pathfinder;
import xyz.oli.api.pathing.PathfinderFactory;
import xyz.oli.pathing.model.pathing.finder.PathfinderImpl;

public class PathfinderFactoryImpl implements PathfinderFactory {

    @Override
    public Pathfinder newPathfinder(PathfinderOptions options) {
        return new PathfinderImpl(options);
    }

}
