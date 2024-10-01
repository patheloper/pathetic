package org.patheloper.util;

import lombok.extern.slf4j.Slf4j;
import org.patheloper.Pathetic;

@Slf4j
public class ErrorLogger {

  public static IllegalStateException logFatalError(String message) {
    return logFatalError(message, null);
  }

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

  public static IllegalStateException logFatalErrorWithStacktrace(String message, Throwable cause) {
    IllegalStateException exception = logFatalError(message, cause);
    log.error("Stacktrace:", cause);
    return exception;
  }
}
