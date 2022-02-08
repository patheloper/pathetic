package xyz.oli.pathing.model.finder.factory;

import xyz.oli.api.pathing.Pathfinder;
import xyz.oli.api.pathing.factory.PathfinderFactory;
import xyz.oli.pathing.model.finder.PathfinderImpl;

public class PathfinderFactoryImpl implements PathfinderFactory {

    @Override
    public Pathfinder newPathfinder() {
        return new PathfinderImpl();
    }

}
