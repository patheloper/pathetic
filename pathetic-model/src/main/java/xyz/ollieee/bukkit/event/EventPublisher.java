package xyz.ollieee.bukkit.event;

import org.bukkit.Bukkit;
import xyz.ollieee.api.event.PathingEvent;

public class EventPublisher {

    public static void raiseEvent(PathingEvent pathingEvent) {
        Bukkit.getPluginManager().callEvent(pathingEvent);
    }

}
