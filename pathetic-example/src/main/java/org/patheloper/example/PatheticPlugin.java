package org.patheloper.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.example.command.PatheticCommand;
import org.patheloper.paper.PatheticPaper;

public final class PatheticPlugin extends JavaPlugin {

  @Override
  public void onEnable() {

    // Before using Pathetic, you need to initialize it.
    PatheticPaper.getInstance().initialize(this);

    // Then you can use the PatheticMapper to get your own Pathfinder instance with your own set
    // rules.
    Pathfinder reusablePathfinder =
      PatheticPaper.getInstance().newPathfinder(
            PathingRuleSet.createAsyncRuleSet()
                .withAllowingFailFast(true)
                .withAllowingFallback(true)
                .withLoadingChunks(true));

    this.getCommand("pathetic").setExecutor(new PatheticCommand(reusablePathfinder));
  }
}
