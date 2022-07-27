package xyz.ollieee.model.finder.handler;

import org.bukkit.Bukkit;

public class PathfinderAsyncExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        StringBuilder visualizeStacktrace = new StringBuilder("Exception caught in PathfinderAsyncHandler:").append("\n");
        visualizeStacktrace.append(e.toString()).append("\n");
        for (StackTraceElement stackTraceElement : e.getStackTrace())
            visualizeStacktrace.append("\tat: ").append(stackTraceElement.toString()).append("\n");

        Bukkit.getLogger().severe(visualizeStacktrace.toString());
    }
}
