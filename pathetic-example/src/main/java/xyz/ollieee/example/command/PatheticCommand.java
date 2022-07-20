package xyz.ollieee.example.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.ollieee.api.pathing.Pathfinder;
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

        if(!(sender instanceof Player))
            return false;

        if(args.length != 1)
            return false;

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!sessionMap.containsKey(uuid))
            sessionMap.put(uuid, new PlayerSession());

        PlayerSession playerSession = sessionMap.get(uuid);
        if(args[0].equals("pos1")) {
            player.sendMessage("Set pos1!");
            playerSession.setPos1(player.getLocation());
        } else if(args[0].equals("pos2")) {
            player.sendMessage("Set pos2!");
            playerSession.setPos2(player.getLocation());
        } else { // "/pathetic trololol"

            if(!playerSession.isComplete()) {
                player.sendMessage("Set pos1 and/or pos2 first!");
                return false;
            }

            player.sendMessage("Looking for a path...");
            pathfinder.findPathAsync(BukkitMapper.toPathLocation(playerSession.getPos1()), BukkitMapper.toPathLocation(playerSession.getPos2()))
                    .thenAccept(pathfinderResult -> {

                        player.sendMessage("PathfinderStatus: " + pathfinderResult.getPathfinderSuccess());
                        if(pathfinderResult.successful())
                            for(PathLocation pathLocation : pathfinderResult.getPath().getLocations())
                                player.sendBlockChange(BukkitMapper.toLocation(pathLocation), Material.YELLOW_STAINED_GLASS.createBlockData());
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
