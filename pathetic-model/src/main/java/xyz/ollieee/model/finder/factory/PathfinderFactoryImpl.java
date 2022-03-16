package xyz.ollieee.model.finder.factory;

import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.factory.PathfinderFactory;
import xyz.ollieee.model.finder.PathfinderImpl;

public class PathfinderFactoryImpl implements PathfinderFactory {

    @Override
    public Pathfinder newPathfinder() {
        return new PathfinderImpl();
    }

}
