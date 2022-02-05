package xyz.oli.pathing.model.path.finder;

import lombok.NonNull;

import org.bukkit.Location;

import xyz.oli.options.PathfinderOptions;
import xyz.oli.pathing.PathResult;
import xyz.oli.pathing.PathfinderStrategy;
import xyz.oli.pathing.strategies.DirectPathfinderStrategy;
import xyz.oli.pathing.util.PathingScheduler;
import xyz.oli.wrapper.BukkitConverter;

import java.util.concurrent.CompletableFuture;
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

    @Override
    public CompletableFuture<PathResult> findPath(@NonNull Location startLocation, @NonNull Location targetLocation) {
        return this.findPath(startLocation, targetLocation, new DirectPathfinderStrategy());
    }

    @Override
    public CompletableFuture<PathResult> findPath(@NonNull Location startLocation, @NonNull Location targetLocation, @NonNull PathfinderStrategy strategy) {
        return PathingScheduler.supplyAsync(() -> pathFinder.findPath(BukkitConverter.toPathLocation(startLocation), BukkitConverter.toPathLocation(targetLocation), strategy));
    }

    @Override
    public PathResult findPathNow(@NonNull Location startLocation, @NonNull Location targetLocation) {
        return this.findPathNow(startLocation, targetLocation, new DirectPathfinderStrategy());
    }

    @Override
    public PathResult findPathNow(@NonNull Location startLocation, @NonNull Location targetLocation, @NonNull PathfinderStrategy strategy) {
        return pathFinder.findPath(BukkitConverter.toPathLocation(startLocation), BukkitConverter.toPathLocation(targetLocation), strategy);
    }
}
