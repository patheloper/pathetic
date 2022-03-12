package xyz.oli;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.oli.api.material.MaterialParser;
import xyz.oli.api.pathing.factory.PathfinderFactory;
import xyz.oli.api.pathing.world.chunk.SnapshotManager;
import xyz.oli.bstats.BStatsHandler;
import xyz.oli.legacy.material.LegacyMaterialParser;
import xyz.oli.model.finder.factory.PathfinderFactoryImpl;
import xyz.oli.model.world.chunk.SnapshotManagerImpl;
import xyz.oli.model.world.material.ModernMaterialParser;
import xyz.oli.util.BukkitVersionUtil;

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
    }
    
    public static JavaPlugin getPluginInstance() {
        return instance;
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
