package xyz.oli.pathing;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.pathing.commands.PathfindingCommand;

public class PathfindingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        getCommand("finder").setExecutor(new PathfindingCommand());

        getLogger().info("Plugin Is Enabled");
    }
}
