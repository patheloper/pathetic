package org.patheloper.util;

import lombok.extern.slf4j.Slf4j;
import org.patheloper.Pathetic;

/**
 * A utility class for logging fatal errors in the application and providing relevant information
 * for debugging purposes.
 */
@Slf4j
public class ErrorLogger {

  /**
   * Logs a fatal error with system details and a message.
   *
   * @param message the error message to log
   * @return an {@link IllegalStateException} with the given message
   */
  public static IllegalStateException logFatalError(String message) {
    return logFatalError(message, null);
  }

  /**
   * Logs a fatal error with system details, a message, and a root cause.
   *
   * @param message the error message to log
   * @param cause the {@link Throwable} cause of the error, can be null
   * @return an {@link IllegalStateException} with the given message and cause
   */
  public static IllegalStateException logFatalError(String message, Throwable cause) {
    log.error("===============================");
    log.error("A fatal error has occurred: {}", message);
    log.error("Please open an issue on the Pathetic GitHub page with all this information:");
    log.error("Version: {}", Pathetic.getModelVersion());
    log.error("Server Version: {}", Pathetic.getPluginInstance().getServer().getVersion());
    log.error("Java Version: {}", System.getProperty("java.version"));
    log.error("OS: {}", System.getProperty("os.name"));
    log.error("OS Architecture: {}", System.getProperty("os.arch"));
    log.error("===============================");
    return new IllegalStateException(message, cause);
  }

  /**
   * Logs a fatal error with system details, a message, and a root cause, and logs the stacktrace.
   *
   * @param message the error message to log
   * @param cause the {@link Throwable} cause of the error
   * @return an {@link IllegalStateException} with the given message and cause
   */
  public static IllegalStateException logFatalErrorWithStacktrace(String message, Throwable cause) {
    IllegalStateException exception = logFatalError(message, cause);
    log.error("Stacktrace:", cause);
    return exception;
  }
}
