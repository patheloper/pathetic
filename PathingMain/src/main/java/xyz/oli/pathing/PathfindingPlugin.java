package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.oli.PathingAPI;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.material.legacy.LegacyMaterialHandler;
import xyz.oli.pathing.model.path.finder.PathfinderImpl;
import xyz.oli.pathing.model.path.finder.strategy.chunks.SnapshotManager;
import xyz.oli.pathing.model.path.finder.strategy.chunks.materialparser.ModernMaterialParser;
import xyz.oli.pathing.util.BukkitVersionUtil;
import xyz.oli.pathing.util.PathingScheduler;

import java.util.logging.Logger;

public class PathfindingPlugin extends JavaPlugin {

    private static Logger logger;

    @Override
    public void onLoad() {
        setup();
    }

    private void setup() {
        logger = getLogger();

        PathingScheduler.setPlugin(this);

        MaterialParser parser;

        if (BukkitVersionUtil.isUnder(13)) parser = new LegacyMaterialHandler();
        else parser = new ModernMaterialParser();

        PathingAPI.setFields(parser, new SnapshotManager(), new PathfinderImpl());
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
