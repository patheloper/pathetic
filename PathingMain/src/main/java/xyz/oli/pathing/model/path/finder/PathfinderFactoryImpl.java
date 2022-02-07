package xyz.oli.pathing.model.path.finder;

import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.model.path.finder.pathfinder.PathfinderImpl;

public class PathfinderFactoryImpl implements xyz.oli.pathing.PathfinderFactory {

    @Override
    public Pathfinder newPathfinder() {
        return new PathfinderImpl();
    }

}
