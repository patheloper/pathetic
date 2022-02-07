package xyz.oli.pathing.model.pathing.finder.factory;

import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.model.pathing.finder.PathfinderImpl;

public class PathfinderFactoryImpl implements xyz.oli.pathing.PathfinderFactory {

    @Override
    public Pathfinder newPathfinder() {
        return new PathfinderImpl();
    }

}
