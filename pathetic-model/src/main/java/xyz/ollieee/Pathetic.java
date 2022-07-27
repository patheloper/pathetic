package xyz.ollieee;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ollieee.api.material.MaterialParser;
import xyz.ollieee.api.pathing.world.chunk.SnapshotManager;
import xyz.ollieee.bstats.BStatsHandler;
import xyz.ollieee.bukkit.event.PathingEventListener;
import xyz.ollieee.legacy.material.LegacyMaterialParser;
import xyz.ollieee.model.world.chunk.SnapshotManagerImpl;
import xyz.ollieee.model.world.material.ModernMaterialParser;
import xyz.ollieee.util.BukkitVersionUtil;

import java.util.logging.Logger;

@UtilityClass
public class Pathetic {

    private static JavaPlugin instance;
    private static Logger logger;

    // We maybe dont want them here.
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
        logger.info("PatheticAPI successfully initialized");
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
