package xyz.ollieee.example.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.result.task.PathingTask;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.mapping.bukkit.BukkitMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

public class PatheticCommand implements TabExecutor {

    private static final Map<UUID, PlayerSession> sessionMap = new HashMap<>();

    private final Pathfinder pathfinder;

    public PatheticCommand(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        if (args.length != 1)
            return false;

        Player player = (Player) sender;
        PlayerSession playerSession = sessionMap.computeIfAbsent(player.getUniqueId(),
                k -> new PlayerSession());

        switch (args[0]) {
            case "pos1":

                playerSession.setPos1(player.getLocation());
                player.sendMessage("Position 1 set to " + player.getLocation());

                break;
            case "pos2":

                playerSession.setPos2(player.getLocation());
                player.sendMessage("Position 2 set to " + player.getLocation());

                break;
            case "start":

                if(!playerSession.isComplete()) {
                    player.sendMessage("Set both positions first!");
                    return false;
                }

                PathLocation start = BukkitMapper.toPathLocation(playerSession.getPos1());
                PathLocation target = BukkitMapper.toPathLocation(playerSession.getPos2());

                player.sendMessage("Starting pathfinding...");
                PathingTask pathingTask = pathfinder.findPath(start, target);

                pathingTask.accept(result -> {

                    player.sendMessage("State: " + result.getPathfinderState().name());
                    player.sendMessage("Path length: " + result.getPath().length());

                    if(result.successful()) {

                        result.getPath().getLocations().forEach(pathLocation -> {
                            Location location = BukkitMapper.toLocation(pathLocation);
                            player.sendBlockChange(location, Material.YELLOW_STAINED_GLASS.createBlockData());
                        });
                    } else {
                        player.sendMessage("Path not found!");
                    }
                });

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Arrays.asList("pos1", "pos2", "start");
    }

    private static class PlayerSession {

        private Location pos1;
        private Location pos2;

        public void setPos1(Location pos1) {
            this.pos1 = pos1;
        }

        public void setPos2(Location pos2) {
            this.pos2 = pos2;
        }

        public boolean isComplete() {
            return pos1 != null && pos2 != null;
        }

        public Location getPos1() {
            return pos1;
        }

        public Location getPos2() {
            return pos2;
        }
    }
}
