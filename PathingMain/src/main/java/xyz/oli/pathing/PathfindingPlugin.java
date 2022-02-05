package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.oli.PathingAPI;
import xyz.oli.pathing.material.legacy.LegacyMaterialHandler;
import xyz.oli.pathing.model.path.finder.PathfinderImpl;
import xyz.oli.pathing.model.path.finder.strategy.chunks.SnapshotManager;
import xyz.oli.pathing.model.path.finder.strategy.chunks.materialparser.ModernMaterialParser;
import xyz.oli.pathing.util.BukkitVersionUtil;

public class PathfindingPlugin extends JavaPlugin {

    static PathfindingPlugin instance;

    @Override
    public void onLoad() {
        instance = this;

        if (BukkitVersionUtil.isUnder(13)) {
            PathingAPI.setParser(new LegacyMaterialHandler());
        } else {
            PathingAPI.setParser(new ModernMaterialParser());
        }

        PathingAPI.setSnapshotManager(new SnapshotManager());

        PathingAPI.setPathfinder(new PathfinderImpl());
    }

    public static PathfindingPlugin getInstance() {
        return instance;
    }
}
