package org.patheloper.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.strategies.WalkablePathfinderStrategy;
import org.patheloper.example.command.PatheticCommand;
import org.patheloper.mapping.PatheticMapper;

public final class PatheticPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        PatheticMapper.initialize(this);

        Pathfinder reusablePathfinder = PatheticMapper.newPathfinder(PathingRuleSet.createAsyncRuleSet()
                .withStrategy(WalkablePathfinderStrategy.class)
                .withAllowingDiagonal(true)
                .withAllowingFailFast(true)
                .withAllowingFallback(true)
                .withLoadingChunks(true));

        getCommand("pathetic").setExecutor(new PatheticCommand(reusablePathfinder));
    }
}
