package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.pathing.commands.PathfindingCommand;

public class PathfindingPlugin extends JavaPlugin {

    static PathfindingPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("finder").setExecutor(new PathfindingCommand());

        getLogger().info("Plugin Is Enabled");
    }

    public static PathfindingPlugin getInstance() {
        return instance;
    }
}
