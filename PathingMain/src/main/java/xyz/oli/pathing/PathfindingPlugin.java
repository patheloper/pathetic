package xyz.oli.pathing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.oli.pathing.material.legacy.LegacyMaterialHandler;
import xyz.oli.pathing.material.MaterialParser;
import xyz.oli.pathing.model.path.finder.strategy.chunks.materialparser.ModernMaterialParser;

public class PathfindingPlugin extends JavaPlugin {

    static PathfindingPlugin instance;
    private MaterialParser parser;

    @Override
    public void onEnable() {
        instance = this;

        if (Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]) < 13) {
            parser = new LegacyMaterialHandler();
        }else {
            parser = new ModernMaterialParser();
        }

        getLogger().info("Plugin Is Enabled");
    }

    public static PathfindingPlugin getInstance() {
        return instance;
    }

    public MaterialParser getParser() {
        return parser;
    }
}
