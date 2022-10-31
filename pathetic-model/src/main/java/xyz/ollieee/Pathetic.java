package xyz.ollieee;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.bstats.BStatsHandler;
import xyz.ollieee.bukkit.event.PathingEventListener;
import xyz.ollieee.bukkit.listeners.ChunkInvalidateListener;
import xyz.ollieee.nms.NMSUtils;
import xyz.ollieee.util.BukkitVersionUtil;

import java.util.logging.Logger;

@UtilityClass
public class Pathetic {

    private static final NMSUtils nmsUtils;

    private static JavaPlugin instance;
    private static Logger logger;

    static {
        nmsUtils = new NMSUtils((int) BukkitVersionUtil.get());
    }

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

    public static NMSUtils getNMSUtils() {
        return nmsUtils;
    }
}
