/**
 * This package contains classes related to the event system of pathetic.
 *
 * <p>The event system is built around the Guava EventBus. It provides a way to define, raise, and
 * handle custom events within the application. The main class in this package is the
 * EventPublisher, which provides static methods to raise events and register/unregister listeners.
 *
 * <p>To create a new event, you would extend the PathingEvent class. To handle an event, you would
 * create a method in your listener class that is annotated with @Subscribe and takes a single
 * argument of your event type.
 *
 * <p>For example:
 *
 * <pre>
 * // Define a new event
 * public class CustomEvent extends PathingEvent {
 *     private final String message;
 *
 *     public CustomEvent(String message) {
 *         this.message = message;
 *     }
 *
 *     public String getMessage() {
 *         return message;
 *     }
 * }
 *
 * // Define a listener for the new event
 * public class CustomEventListener {
 *
 *     @Subscribe
 *     public void onCustomEvent(CustomEvent event) {
 *         System.out.println("Received custom event with message: " + event.getMessage());
 *     }
 * }
 *
 * // Register the listener and raise the event
 * EventPublisher.registerListener(new CustomEventListener());
 * EventPublisher.raiseEvent(new CustomEvent("Hello, world!"));
 * </pre>
 */
package org.patheloper.api.event;
