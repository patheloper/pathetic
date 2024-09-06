package org.patheloper.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.configuration.PathfinderConfiguration;
import org.patheloper.example.command.PatheticCommand;
import org.patheloper.mapping.PatheticMapper;

public final class PatheticPlugin extends JavaPlugin {

  // Called when the plugin is enabled
  @Override
  public void onEnable() {

    // Initialize the PatheticMapper with this plugin instance
    PatheticMapper.initialize(this);

    // Create a new Pathfinder instance with a custom configuration
    Pathfinder reusablePathfinder =
        PatheticMapper.newPathfinder(
            PathfinderConfiguration.createConfiguration()
                .withAllowingFailFast(true) // Allow pathfinding to fail fast if necessary
                .withAllowingFallback(true) // Allow fallback strategies if the primary fails
                .withLoadingChunks(true) // Allow chunks to be loaded during pathfinding
            );

    // Register the command executor for the "pathetic" command
    getCommand("pathetic").setExecutor(new PatheticCommand(reusablePathfinder));
  }

  // Called when the plugin is disabled
  @Override
  public void onDisable() {
    // Shutdown the PatheticMapper to clear any resources it holds
    PatheticMapper.shutdown();
  }
}
