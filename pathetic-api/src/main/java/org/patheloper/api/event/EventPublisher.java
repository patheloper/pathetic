package org.patheloper.api.event;

import com.google.common.eventbus.EventBus;
import lombok.experimental.UtilityClass;

/**
 * A utility class for managing the publication and subscription of events in the system.
 *
 * <p>This class leverages the {@link EventBus} from Guava to post events and manage event listeners.
 * It provides static methods for raising events and registering/unregistering listeners to handle
 * these events. The class acts as a centralized event manager.
 */
@UtilityClass
public class EventPublisher {

  /** The internal {@link EventBus} instance used to manage event publication and listeners. */
  private static final EventBus eventBus = new EventBus();

  /**
   * Publishes an event to the {@link EventBus}.
   *
   * <p>This method is used to raise a {@link PathingEvent}. The event bus will notify all listeners subscribed to events of the
   * same type as the posted event.
   *
   * @param pathingEvent the {@link PathingEvent} to be raised
   */
  public static void raiseEvent(PathingEvent pathingEvent) {
    eventBus.post(pathingEvent);
  }

  /**
   * Registers an object as an event listener.
   *
   * <p>The provided object will be registered to listen for events. The object must have methods
   * annotated with {@link com.google.common.eventbus.Subscribe} in order to receive events from the
   * {@link EventBus}.
   *
   * @param listener the listener object to register
   */
  public static void registerListener(Object listener) {
    eventBus.register(listener);
  }

  /**
   * Unregisters an object from receiving events.
   * @param listener the listener object to unregister
   */
  public static void unregisterListener(Object listener) {
    eventBus.unregister(listener);
  }
}
