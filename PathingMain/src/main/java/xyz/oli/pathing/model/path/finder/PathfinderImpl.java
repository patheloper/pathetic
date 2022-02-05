package xyz.oli.pathing.model.path.finder;

import lombok.NonNull;
import xyz.oli.options.PathfinderOptions;
import xyz.oli.pathing.PathResult;
import xyz.oli.pathing.util.PathingScheduler;
import xyz.oli.wrapper.BukkitConverter;

import java.util.function.Consumer;

public class PathfinderImpl implements xyz.oli.pathing.Pathfinder {

    private final Pathfinder pathFinder;

    public PathfinderImpl() {
        this.pathFinder = new Pathfinder();
    }

    @Override
    public void findPath(@NonNull PathfinderOptions pathfinderOptions, @NonNull Consumer<PathResult> callback) {

        if (pathfinderOptions.isAsyncMode()) {
            PathingScheduler.runAsync(() -> pathFinder.findPath(BukkitConverter.toPathLocation(pathfinderOptions.getStart()), BukkitConverter.toPathLocation(pathfinderOptions.getTarget()), pathfinderOptions.getStrategy()), callback);
            return;
        }

        PathfinderResult path = pathFinder.findPath(BukkitConverter.toPathLocation(pathfinderOptions.getStart()), BukkitConverter.toPathLocation(pathfinderOptions.getTarget()), pathfinderOptions.getStrategy());
        callback.accept(path);
    }
}
