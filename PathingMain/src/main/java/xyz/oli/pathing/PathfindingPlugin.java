package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.oli.pathing.material.legacy.LegacyMaterialHandler;
import xyz.oli.pathing.material.MaterialParser;
import xyz.oli.pathing.model.path.finder.strategy.chunks.materialparser.ModernMaterialParser;
import xyz.oli.pathing.util.BukkitVersionUtil;

public class PathfindingPlugin extends JavaPlugin {

    static PathfindingPlugin instance;
    private MaterialParser parser;

    @Override
    public void onEnable() {
        instance = this;

        if (BukkitVersionUtil.isUnder(13)) {
            parser = new LegacyMaterialHandler();
        } else {
            parser = new ModernMaterialParser();
        }

        getLogger().info("Plugin Is Enabled"); // lol
    }

    public static PathfindingPlugin getInstance() {
        return instance;
    }

    public MaterialParser getParser() {
        return parser;
    }
}
