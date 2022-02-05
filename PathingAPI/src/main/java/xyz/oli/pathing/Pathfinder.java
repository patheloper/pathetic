package xyz.oli.pathing;

import lombok.NonNull;
import xyz.oli.options.PathfinderOptions;

import java.util.function.Consumer;

public interface Pathfinder {

    void findPath(@NonNull PathfinderOptions pathfinderOptions, @NonNull Consumer<PathResult> callback);
}
