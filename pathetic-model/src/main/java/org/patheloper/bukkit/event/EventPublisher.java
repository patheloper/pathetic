package org.patheloper.bukkit.event;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.patheloper.Pathetic;
import org.patheloper.api.event.PathingEvent;

@UtilityClass
public class EventPublisher {

  public static void raiseEvent(PathingEvent pathingEvent) {

    if (!Bukkit.isPrimaryThread()) {
      Bukkit.getScheduler().runTask(Pathetic.getPluginInstance(), () -> raiseEvent(pathingEvent));
      return;
    }

    Bukkit.getPluginManager().callEvent(pathingEvent);
  }
}
