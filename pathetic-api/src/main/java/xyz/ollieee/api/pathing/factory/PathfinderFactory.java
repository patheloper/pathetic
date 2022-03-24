package xyz.ollieee.api.pathing.factory;

import lombok.NonNull;
import xyz.ollieee.api.pathing.Pathfinder;

public interface PathfinderFactory {

    /**
     * Instantiates and returns a new {@link Pathfinder}
     * @return A new {@link Pathfinder} instance
     */
    @NonNull
    Pathfinder newPathfinder();
}
