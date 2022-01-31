package xyz.oli.pathing;

import java.util.concurrent.ForkJoinPool;

public class PathingAPI {

    protected static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public static PathFinder instantiateNewFinder() {
        return new PathFinder();
    }

}
