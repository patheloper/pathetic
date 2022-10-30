package xyz.ollieee;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.api.snapshot.MaterialParser;
import xyz.ollieee.bstats.BStatsHandler;
import xyz.ollieee.bukkit.event.PathingEventListener;
import xyz.ollieee.legacy.snapshot.LegacyMaterialParser;
import xyz.ollieee.model.snapshot.world.ModernMaterialParser;
import xyz.ollieee.nms.NMSUtils;
import xyz.ollieee.util.BukkitVersionUtil;

import java.util.logging.Logger;

@UtilityClass
public class Pathetic {

    private static final NMSUtils nmsUtils;
    private static final MaterialParser materialParser;

    private static JavaPlugin instance;
    private static Logger logger;

    static {

        nmsUtils = new NMSUtils((int) BukkitVersionUtil.get());

        if (BukkitVersionUtil.isUnder(13))
            materialParser = new LegacyMaterialParser();
        else
            materialParser = new ModernMaterialParser();
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
        // TODO
        //Bukkit.getPluginManager().registerEvents(new ChunkInvalidateListener(snapshotManager), javaPlugin);
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

    public static MaterialParser getMaterialParser() {
        return materialParser;
    }

    public static NMSUtils getNMSUtils() {
        return nmsUtils;
    }
}
