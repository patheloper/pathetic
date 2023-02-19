package org.patheloper;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.bukkit.listeners.ChunkInvalidateListener;

@UtilityClass
public class Pathetic {

    private static JavaPlugin instance;

    /**
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    public static void initialize(JavaPlugin javaPlugin) {

        if(instance != null)
            throw new IllegalStateException("Can't be initialized twice");

        instance = javaPlugin;

        Bukkit.getPluginManager().registerEvents(new ChunkInvalidateListener(), javaPlugin);

        javaPlugin.getLogger().info("pathetic successfully initialized");
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static JavaPlugin getPluginInstance() {
        return instance;
    }
}
