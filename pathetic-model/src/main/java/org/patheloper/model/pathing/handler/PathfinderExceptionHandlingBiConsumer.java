package org.patheloper.model.pathing.handler;

import lombok.extern.log4j.Log4j2;

import java.util.function.BiConsumer;

@Log4j2
public class PathfinderExceptionHandlingBiConsumer<T> implements BiConsumer<T, Throwable> {

    @Override
    public void accept(T t, Throwable throwable) {
        if(throwable != null)
            log.error("An exception occurred while pathfinding", throwable);
    }
}
