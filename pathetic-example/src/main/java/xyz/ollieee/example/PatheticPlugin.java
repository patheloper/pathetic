package xyz.ollieee.example;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.api.pathing.strategy.strategies.WalkablePathfinderStrategy;
import xyz.ollieee.example.command.PatheticCommand;
import xyz.ollieee.mapping.PatheticMapper;

public final class PatheticPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        PatheticMapper.initialize(this);

        Pathfinder reusablePathfinder = PatheticMapper.newPathfinder(PathingRuleSet.createAsyncRuleSet()
                .withStrategy(new WalkablePathfinderStrategy())
                .withAllowingDiagonal(true)
                .withAllowingFailFast(true)
                .withAllowingFallback(true)
                .withLoadingChunks(true));

        getCommand("pathetic").setExecutor(new PatheticCommand(reusablePathfinder));
    }
}
