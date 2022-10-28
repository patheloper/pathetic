package xyz.ollieee.example.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.mapping.bukkit.BukkitMapper;

import java.util.*;

public class PatheticCommand implements TabExecutor {

    private final Map<UUID, PlayerSession> sessionMap = new HashMap<>();
    private final Pathfinder pathfinder;

    public PatheticCommand(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Don't do nothing if the executor isn't a Player
        if(!(sender instanceof Player))
            return false;

        // Check if the command has one and only one argument
        if(args.length != 1)
            return false;

        // Our variables to work with
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // Check if the player don't have a PlayerSession yet
        if(!sessionMap.containsKey(uuid))
            sessionMap.put(uuid, new PlayerSession()); // Give him one if not

        // Go through the PlayerSession and set the respective positions
        PlayerSession playerSession = sessionMap.get(uuid);
        if(args[0].equals("pos1")) {
            player.sendMessage("Set pos1!");
            playerSession.setPos1(player.getLocation());
        } else if(args[0].equals("pos2")) {
            player.sendMessage("Set pos2!");
            playerSession.setPos2(player.getLocation());
        } else { // "/pathetic trololol"

            // Check if the PlayerSession has both needed positions assigned
            if (!playerSession.isComplete()) {
                player.sendMessage("Set pos1 and/or pos2 first!");
                return false;
            }

            // Use the positions we just checked for to search asynchronous for a Path between them
            player.sendMessage("Looking for a path...");
            pathfinder.findPath(BukkitMapper.toPathLocation(playerSession.getPos1()), BukkitMapper.toPathLocation(playerSession.getPos2()))
                    .accept(pathfinderResult -> { // Which will always return a PathfinderResult, so we accept on that

                        // Printing out the PathfinderSuccess which can either be FAILED or FOUND
                        player.sendMessage("PathfinderStatus: " + pathfinderResult.getPathfinderState());
                        player.sendMessage("Pathlength: " + pathfinderResult.getPath().length());
                        for (PathLocation pathLocation : pathfinderResult.getPath().getLocations()) // Interact on that Path
                            player.sendBlockChange(BukkitMapper.toLocation(pathLocation), Material.YELLOW_STAINED_GLASS.createBlockData()); // And send weird stuff to the Player
                    });
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Arrays.asList("pos1", "pos2");
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
