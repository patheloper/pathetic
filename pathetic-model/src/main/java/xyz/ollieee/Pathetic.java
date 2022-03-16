package xyz.ollieee;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ollieee.api.material.MaterialParser;
import xyz.ollieee.api.pathing.factory.PathfinderFactory;
import xyz.ollieee.api.pathing.world.chunk.SnapshotManager;
import xyz.ollieee.bstats.BStatsHandler;
import xyz.ollieee.legacy.material.LegacyMaterialParser;
import xyz.ollieee.model.finder.factory.PathfinderFactoryImpl;
import xyz.ollieee.model.world.chunk.SnapshotManagerImpl;
import xyz.ollieee.model.world.material.ModernMaterialParser;
import xyz.ollieee.util.BukkitVersionUtil;

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

        MaterialParser parser;
        if (BukkitVersionUtil.isUnder(13)) parser = new LegacyMaterialParser();
        else parser = new ModernMaterialParser();

        Bukkit.getServicesManager().register(PathfinderFactory.class, new PathfinderFactoryImpl(), javaPlugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(MaterialParser.class, parser, javaPlugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(SnapshotManager.class, new SnapshotManagerImpl(), javaPlugin, ServicePriority.Highest);

        BStatsHandler.init(javaPlugin);
        logger.info("PatheticAPI successfully initialized");
    }
    
    public static JavaPlugin getPluginInstance() {
        return instance;
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
