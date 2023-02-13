package org.patheloper.model.pathing.handler;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PathfinderAsyncExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception caught in PathfinderAsyncHandler:", e);
    }
}
