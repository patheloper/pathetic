package xyz.oli.pathing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.api.PatheticAPI;
import xyz.oli.api.material.MaterialParser;
import xyz.oli.api.pathing.factory.PathfinderFactory;
import xyz.oli.pathing.bstats.BStatsHandler;
import xyz.oli.legacy.material.LegacyMaterialParser;
import xyz.oli.pathing.model.finder.factory.PathfinderFactoryImpl;
import xyz.oli.pathing.model.world.chunk.SnapshotManagerImpl;
import xyz.oli.pathing.model.world.material.ModernMaterialParser;
import xyz.oli.pathing.util.BukkitVersionUtil;

import java.util.logging.Logger;

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
        
        Bukkit.getServicesManager().register(PathfinderFactory.class, new PathfinderFactoryImpl(), javaPlugin, ServicePriority.Highest);

        MaterialParser parser;
        if (BukkitVersionUtil.isUnder(13)) parser = new LegacyMaterialParser();
        else parser = new ModernMaterialParser();

        BStatsHandler.init(javaPlugin);

        PatheticAPI.setFields(parser, new SnapshotManagerImpl());
    }
    
    public static JavaPlugin getPluginInstance() {
        return instance;
    }
    
    public static Logger getPluginLogger() {
        return logger;
    }
    
    private Pathetic() {}
}
