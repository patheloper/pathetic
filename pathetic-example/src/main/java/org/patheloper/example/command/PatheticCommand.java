package org.patheloper.example.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.mapping.bukkit.BukkitMapper;

public class PatheticCommand implements TabExecutor {

  private static final Map<UUID, PlayerSession> SESSION_MAP = new HashMap<>();

  private final Pathfinder pathfinder;

  public PatheticCommand(Pathfinder pathfinder) {
    this.pathfinder = pathfinder;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player)) return false;

    if (args.length != 1) return false;

    Player player = (Player) sender;
    PlayerSession playerSession =
        SESSION_MAP.computeIfAbsent(player.getUniqueId(), k -> new PlayerSession());

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
        if (!playerSession.isComplete()) {
          player.sendMessage("Set both positions first!");
          return false;
        }

        // Here we convert the Bukkit Locations to PathLocations to search with them for a path.
        PathPosition start = BukkitMapper.toPathPosition(playerSession.getPos1());
        PathPosition target = BukkitMapper.toPathPosition(playerSession.getPos2());

        player.sendMessage("Starting pathfinding...");
        CompletionStage<PathfinderResult> pathfindingResult =
            pathfinder.findPath(
                start, target, new DirectPathfinderStrategy()); // This is the actual pathfinding.

        // This is just a simple way to display the pathfinding result.
        pathfindingResult.thenAccept(
            result -> {
              player.sendMessage("State: " + result.getPathState().name());
              player.sendMessage("Path length: " + result.getPath().length());

              if (result.successful() || result.hasFallenBack()) {

                result
                    .getPath()
                    .forEach(
                        position -> {
                          Location location = BukkitMapper.toLocation(position);
                          player.sendBlockChange(
                              location, Material.YELLOW_STAINED_GLASS.createBlockData());
                        });
              } else {
                player.sendMessage("Path not found!");
              }
            });
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String label, String[] args) {
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
