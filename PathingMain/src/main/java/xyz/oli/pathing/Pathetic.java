package xyz.oli.pathing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.PathingAPI;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.bstats.BStatsHandler;
import xyz.oli.pathing.material.legacy.LegacyMaterialParser;
import xyz.oli.pathing.model.pathing.finder.factory.PathfinderFactoryImpl;
import xyz.oli.pathing.model.pathing.world.chunk.SnapshotManagerImpl;
import xyz.oli.pathing.model.pathing.world.material.parser.ModernMaterialParser;
import xyz.oli.pathing.util.BukkitVersionUtil;
import xyz.oli.pathing.util.PathingScheduler;

import java.util.logging.Logger;

public class Pathetic {

    private static Logger logger;
    
    /**
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    public static void initialize(JavaPlugin javaPlugin) {
    
        RegisteredServiceProvider<PathfinderFactory> registration = Bukkit.getServicesManager().getRegistration(PathfinderFactory.class);
        if(registration != null)
            throw new IllegalStateException("Can't be initialized twice");
        
        logger = javaPlugin.getLogger();
        
        Bukkit.getServicesManager().register(PathfinderFactory.class, new PathfinderFactoryImpl(), javaPlugin, ServicePriority.Highest);
    
        MaterialParser parser;
        if (BukkitVersionUtil.isUnder(13)) parser = new LegacyMaterialParser();
        else parser = new ModernMaterialParser();
    
        BStatsHandler.init(javaPlugin);
        
        PathingAPI.setFields(parser, new SnapshotManagerImpl());
        PathingScheduler.setPlugin(javaPlugin);
    }
    
    public static Logger getPluginLogger() {
        return logger;
    }
}
