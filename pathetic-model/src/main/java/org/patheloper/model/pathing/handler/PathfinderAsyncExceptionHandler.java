package org.patheloper.model.pathing.handler;

import org.apache.logging.log4j.Logger;
import static org.apache.logging.log4j.LogManager.getLogger;

public class PathfinderAsyncExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = getLogger(PathfinderAsyncExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("Exception caught in PathfinderAsyncHandler:", e);
    }
}
