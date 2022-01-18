package xyz.oli.pathing.commands;

import com.google.common.base.Stopwatch;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import xyz.oli.pathing.api.Finder;
import xyz.oli.pathing.model.path.Path;

import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

public class PathfindingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (commandSender instanceof Player player) {
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection().clone().multiply(200);
            eyeLocation.setX(eyeLocation.getX() + direction.getX());
            eyeLocation.setY(eyeLocation.getY() + direction.getY());
            eyeLocation.setZ(eyeLocation.getZ() + direction.getZ());
            CompletableFuture.supplyAsync( () -> {
                Stopwatch timer = Stopwatch.createStarted();
                Path path = Finder.findPath(player.getLocation(), eyeLocation);
                System.out.println("Method took: " + timer.stop());
                return path;
            }).thenAccept(path -> {
                System.out.println(path.getPath().size());
                path.getPath().forEach( location -> player.sendBlockChange(location, Material.YELLOW_STAINED_GLASS.createBlockData()));
            });
        }

         return false;
    }
}
