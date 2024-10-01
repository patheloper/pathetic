package org.patheloper.api.event;

import com.google.common.eventbus.EventBus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventPublisher {

  private static final EventBus eventBus = new EventBus();

  public static void raiseEvent(PathingEvent pathingEvent) {
    eventBus.post(pathingEvent);
  }

  public static void registerListener(Object listener) {
    eventBus.register(listener);
  }

  public static void unregisterListener(Object listener) {
    eventBus.unregister(listener);
  }
}
