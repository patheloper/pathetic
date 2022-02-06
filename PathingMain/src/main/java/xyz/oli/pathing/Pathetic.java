package xyz.oli.pathing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.PathingAPI;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.bstats.BStatsHandler;
import xyz.oli.pathing.material.legacy.LegacyMaterialParser;
import xyz.oli.pathing.model.path.finder.PathfinderFactoryImpl;
import xyz.oli.pathing.model.path.finder.strategy.chunks.SnapshotManager;
import xyz.oli.pathing.model.path.finder.strategy.chunks.materialparser.ModernMaterialParser;
import xyz.oli.pathing.util.BukkitVersionUtil;
import xyz.oli.pathing.util.PathingScheduler;

import java.util.logging.Logger;

public class Pathetic {

    private static Logger logger;
    
    public static void initialize(JavaPlugin javaPlugin) {

        logger = javaPlugin.getLogger();
        
        Bukkit.getServicesManager().register(PathfinderFactory.class, new PathfinderFactoryImpl(), javaPlugin, ServicePriority.Highest);
    
        MaterialParser parser;
        if (BukkitVersionUtil.isUnder(13)) parser = new LegacyMaterialParser();
        else parser = new ModernMaterialParser();
    
        BStatsHandler.init(javaPlugin);
        
        PathingAPI.setFields(parser, new SnapshotManager());
        PathingScheduler.setPlugin(javaPlugin);
    }
    
    public static Logger getPluginLogger() {
        return logger;
    }
}
