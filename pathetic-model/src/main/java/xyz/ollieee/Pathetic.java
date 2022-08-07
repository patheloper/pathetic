package xyz.ollieee;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.bstats.BStatsHandler;
import xyz.ollieee.bukkit.event.PathingEventListener;
import xyz.ollieee.bukkit.listeners.ChunkInvalidateListener;
import xyz.ollieee.legacy.snapshot.LegacyMaterialParser;
import xyz.ollieee.model.snapshot.ModernMaterialParser;
import xyz.ollieee.model.snapshot.SnapshotManagerImpl;
import xyz.ollieee.util.BukkitVersionUtil;

import java.util.logging.Logger;

@UtilityClass
public class Pathetic {

    private static JavaPlugin instance;
    private static Logger logger;

    // We maybe don't want them here.
    private static MaterialParser materialParser;
    private static SnapshotManager snapshotManager;

    /**
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    public static void initialize(JavaPlugin javaPlugin) {

        if(instance != null)
            throw new IllegalStateException("Can't be initialized twice");

        instance = javaPlugin;
        logger = javaPlugin.getLogger();

        if (BukkitVersionUtil.isUnder(13)) materialParser = new LegacyMaterialParser();
        else materialParser = new ModernMaterialParser();

        snapshotManager = new SnapshotManagerImpl();

        BStatsHandler.init(javaPlugin);
        Bukkit.getPluginManager().registerEvents(new PathingEventListener(), javaPlugin);
        Bukkit.getPluginManager().registerEvents(new ChunkInvalidateListener(snapshotManager), javaPlugin);
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

    @Deprecated // for removal
    public static MaterialParser getMaterialParser() {
        return materialParser;
    }

    @Deprecated // for removal
    public static SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }
}
