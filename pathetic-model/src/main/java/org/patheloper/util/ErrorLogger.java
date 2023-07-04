package org.patheloper.util;

import org.patheloper.Pathetic;

import java.util.logging.Logger;

public class ErrorLogger {

    public static IllegalStateException logFatalError(String message) {
        Logger logger = Pathetic.getPluginInstance().getLogger();
        logger.severe("===============================");
        logger.severe("A fatal error has occurred: " + message);
        logger.severe("Please open an issue on the Pathetic GitHub page with all this information:");
        logger.severe("Version: " + Pathetic.getPluginInstance().getDescription().getVersion());
        logger.severe("Server Version: " + Pathetic.getPluginInstance().getServer().getVersion());
        logger.severe("Java Version: " + System.getProperty("java.version"));
        logger.severe("OS: " + System.getProperty("os.name"));
        logger.severe("OS Architecture: " + System.getProperty("os.arch"));
        logger.severe("===============================");
        return new IllegalStateException(message);
    }
}
