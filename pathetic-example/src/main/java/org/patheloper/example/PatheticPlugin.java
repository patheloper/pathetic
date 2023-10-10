package org.patheloper.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import org.patheloper.example.command.PatheticCommand;
import org.patheloper.mapping.PatheticMapper;

public final class PatheticPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        // Before using Pathetic, you need to initialize it.
        PatheticMapper.initialize(this);

        // Then you can use the PatheticMapper to get your own Pathfinder instance with your own set rules.
        Pathfinder reusablePathfinder = PatheticMapper.newPathfinder(PathingRuleSet.createAsyncRuleSet()
                .withAllowingFailFast(true)
                .withAllowingFallback(true)
                .withLoadingChunks(true));

        getCommand("pathetic").setExecutor(new PatheticCommand(reusablePathfinder));
    }
}
