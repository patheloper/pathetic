package xyz.ollieee.example;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.example.command.PatheticCommand;
import xyz.ollieee.mapping.PatheticMapper;

public final class PatheticPlugin extends JavaPlugin {

    private Pathfinder reusablePathfinder;

    @Override
    public void onEnable() {
        PatheticMapper.initialize(this);
        reusablePathfinder = PatheticMapper.newPathfinder(PathingRuleSet.builder().allowFallback(true).async(true).maxPathLength(50).maxIterations(2000).build());
        getCommand("pathetic").setExecutor(new PatheticCommand(reusablePathfinder));
    }

    @Override
    public void onDisable() {

    }
}
