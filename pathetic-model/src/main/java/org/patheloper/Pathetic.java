package org.patheloper;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.bstats.BStatsHandler;
import org.patheloper.bukkit.event.PathingEventListener;
import org.patheloper.bukkit.listeners.ChunkInvalidateListener;

import java.util.logging.Logger;

@UtilityClass
public class Pathetic {

    private static JavaPlugin instance;
    private static Logger logger;

    /**
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    public static void initialize(JavaPlugin javaPlugin) {

        if(instance != null)
            throw new IllegalStateException("Can't be initialized twice");

        instance = javaPlugin;
        logger = javaPlugin.getLogger();

        BStatsHandler.init(javaPlugin);

        Bukkit.getPluginManager().registerEvents(new PathingEventListener(), javaPlugin);
        Bukkit.getPluginManager().registerEvents(new ChunkInvalidateListener(), javaPlugin);

        logger.info("PatheticAPI successfully initialized");
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static JavaPlugin getPluginInstance() {
        return instance;
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
