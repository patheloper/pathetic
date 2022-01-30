package xyz.oli.pathing.commands;

import com.google.common.base.Stopwatch;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import xyz.oli.pathing.api.Finder;
import xyz.oli.pathing.api.PathfinderOptions;
import xyz.oli.pathing.api.PathfinderOptionsBuilder;
import xyz.oli.pathing.model.path.finder.strategy.strategies.*;

import org.jetbrains.annotations.NotNull;

public class PathfindingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (commandSender instanceof Player player) {
            RayTraceResult traceResult = player.rayTraceBlocks(600, FluidCollisionMode.ALWAYS);

            if (traceResult == null || traceResult.getHitBlock() == null || traceResult.getHitBlockFace() == null) return true;

            Location hit = traceResult.getHitBlock().getRelative(traceResult.getHitBlockFace()).getLocation();

            PathfinderOptions options = new PathfinderOptionsBuilder()
                    .start(player.getLocation())
                    .target(hit)
                    .asyncMode(false)
                    .strategy(new DirectPathfinderStrategy())
                    .build();

            Stopwatch timer = Stopwatch.createStarted();
            Finder.findPath(options, pathfinderResult -> {
                System.out.println(pathfinderResult.getPathfinderSuccess());
                System.out.println(pathfinderResult.getPath().getLocations().size());
                System.out.println("Method took : " + timer.stop());
                pathfinderResult.getPath().getLocations().forEach( location -> player.sendBlockChange(location, Material.YELLOW_STAINED_GLASS.createBlockData()));
            });
        }

         return true;
    }
}
