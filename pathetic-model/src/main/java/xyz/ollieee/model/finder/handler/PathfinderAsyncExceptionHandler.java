package xyz.ollieee.model.finder.handler;

import org.bukkit.Bukkit;

public class PathfinderAsyncExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Bukkit.getLogger().severe("Exception in PathfinderAsyncHandler: " + e.getMessage());
    }
}
