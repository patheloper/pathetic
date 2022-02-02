package xyz.oli.backlog;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ForkJoinPool;

@UtilityClass
public class PathingAPI {

    // TODO don't make this protected and this does not belong to this class
    protected static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public PathFinder instantiateNewFinder() {
        return new PathFinder();
    }

}
