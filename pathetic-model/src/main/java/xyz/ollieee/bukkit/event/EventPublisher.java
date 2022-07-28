package xyz.ollieee.bukkit.event;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.event.PathingEvent;

@UtilityClass
public class EventPublisher {

    public static void raiseEvent(PathingEvent pathingEvent) {

        if(!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Pathetic.getPluginInstance(), () -> raiseEvent(pathingEvent));
            return;
        }

        Bukkit.getPluginManager().callEvent(pathingEvent);
    }

}
