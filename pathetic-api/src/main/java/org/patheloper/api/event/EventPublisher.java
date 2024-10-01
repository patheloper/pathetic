package org.patheloper.api.event;

import com.google.common.eventbus.EventBus;
import lombok.experimental.UtilityClass;

/**
 * Utility class for publishing and managing events using the Google Guava EventBus. This class
 * provides methods to raise events and register/unregister event listeners.
 */
@UtilityClass
public class EventPublisher {

  private static final EventBus eventBus = new EventBus();

  /**
   * Raises an event by posting it to the EventBus.
   *
   * @param pathingEvent the event to be raised
   */
  public static void raiseEvent(PathingEvent pathingEvent) {
    eventBus.post(pathingEvent);
  }

  /**
   * Registers an event listener with the EventBus.
   *
   * @param listener the listener to be registered
   */
  public static void registerListener(Object listener) {
    eventBus.register(listener);
  }

  /**
   * Unregisters an event listener from the EventBus.
   *
   * @param listener the listener to be unregistered
   */
  public static void unregisterListener(Object listener) {
    eventBus.unregister(listener);
  }
}
